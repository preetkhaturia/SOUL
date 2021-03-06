/*
SOUL: Scala Oversampling and Undersampling Library.
Copyright (C) 2019 Néstor Rodríguez, David López

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation in version 3 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package soul.algorithm.undersampling

import soul.data.Data
import soul.util.KDTree
import soul.util.Utilities.Distance.Distance
import soul.util.Utilities._

import scala.collection.mutable.ArrayBuffer

/** Neighbourhood Cleaning Rule. Original paper: "Improving Identification of Difficult Small Classes by Balancing Class
  * Distribution" by J. Laurikkala.
  *
  * @param data       data to work with
  * @param seed       seed to use. If it is not provided, it will use the system time
  * @param dist       object of Distance enumeration representing the distance to be used
  * @param k          number of neighbours to use when computing k-NN rule (normally 3 neighbours)
  * @param threshold  consider a class to be undersampled if the number of instances of this class is
  *                   greater than data.size * threshold
  * @param normalize  normalize the data or not
  * @param randomData iterate through the data randomly or not
  * @param verbose    choose to display information about the execution or not
  * @author Néstor Rodríguez Vico
  */
class NCL(data: Data, seed: Long = System.currentTimeMillis(), dist: Distance = Distance.EUCLIDEAN, k: Int = 3,
          threshold: Double = 0.5, normalize: Boolean = false, randomData: Boolean = false, verbose: Boolean = false) {
  /** Compute the NCL algorithm.
    *
    * @return undersampled data structure
    */
  def compute(): Data = {
    // Note: the notation used to refers the subsets of data is the used in the original paper.
    val initTime: Long = System.nanoTime()

    val counter: Map[Any, Int] = data.y.groupBy(identity).mapValues(_.length)
    val untouchableClass: Any = counter.minBy((c: (Any, Int)) => c._2)._1
    val random: scala.util.Random = new scala.util.Random(seed)
    var dataToWorkWith: Array[Array[Double]] = if (normalize) zeroOneNormalization(data, data.processedData) else data.processedData
    val classesToWorkWith: Array[Any] = if (randomData) {
      val randomIndex: List[Int] = random.shuffle(data.y.indices.toList)
      dataToWorkWith = (randomIndex map dataToWorkWith).toArray
      (randomIndex map data.y).toArray
    } else {
      data.y
    }

    val (attrCounter, attrClassesCounter, sds) = if (dist == Distance.HVDM) {
      (dataToWorkWith.transpose.map((column: Array[Double]) => column.groupBy(identity).mapValues(_.length)),
        dataToWorkWith.transpose.map((attribute: Array[Double]) => occurrencesByValueAndClass(attribute, data.y)),
        dataToWorkWith.transpose.map((column: Array[Double]) => standardDeviation(column)))
    } else {
      (null, null, null)
    }

    val minorityIndex: ArrayBuffer[Int] = new ArrayBuffer[Int](0)
    val majorityIndex: ArrayBuffer[Int] = new ArrayBuffer[Int](0)

    var i = 0
    while (i < classesToWorkWith.length) {
      if (classesToWorkWith(i) == untouchableClass) minorityIndex += i else majorityIndex += i
      i += 1
    }

    // ENN can not be applied when only one class is in the less important group
    val indexA1: Array[Int] = if (classesToWorkWith.distinct.length > 2) {
      val ennData = new Data(toXData((majorityIndex map dataToWorkWith).toArray), (majorityIndex map classesToWorkWith).toArray, None, data.fileInfo)
      ennData.processedData = (majorityIndex map dataToWorkWith).toArray
      val enn = new ENN(ennData, dist = dist, k = k)
      val resultENN: Data = enn.compute()
      classesToWorkWith.indices.diff(resultENN.index.get).toArray
    } else {
      new Array[Int](0)
    }

    val uniqueMajClasses = (majorityIndex map classesToWorkWith).distinct
    val ratio: Double = dataToWorkWith.length * threshold

    val KDTree: Option[KDTree] = if (dist == Distance.EUCLIDEAN) {
      Some(new KDTree((minorityIndex map dataToWorkWith).toArray, (majorityIndex map classesToWorkWith).toArray, dataToWorkWith(0).length))
    } else {
      None
    }

    def selectNeighbours(l: Int): ArrayBuffer[Int] = {
      var selectedElements = new ArrayBuffer[Int](0)
      val (_, labels, index) = KDTree.get.nNeighbours(dataToWorkWith(l), k)
      val label = mode(labels.toArray)

      if (label != classesToWorkWith(l)) {
        index.foreach { n =>
          if (classesToWorkWith(n) != untouchableClass && counter(classesToWorkWith(n)) > ratio) {
            selectedElements += n
          }
        }
      }
      selectedElements
    }

    def selectNeighboursHVDM(l: Int): ArrayBuffer[Int] = {
      val selectedElements = new ArrayBuffer[Int]()
      val (label, nNeighbours, _) = nnRuleHVDM(dataToWorkWith, dataToWorkWith(l), l, classesToWorkWith, k, data.fileInfo.nominal,
        sds, attrCounter, attrClassesCounter, "nearest")

      if (label != classesToWorkWith(l)) {
        nNeighbours.foreach { n =>
          val nNeighbourClass: Any = classesToWorkWith(n)
          if (nNeighbourClass != untouchableClass && counter(nNeighbourClass) > ratio) {
            selectedElements += n
          }
        }
      }
      selectedElements
    }

    var j = 0
    val indexA2 = new ArrayBuffer[Int](0)
    while (j < uniqueMajClasses.length) {
      val selectedNeighbours: Array[ArrayBuffer[Int]] = if (dist == Distance.EUCLIDEAN) {
        minorityIndex.par.map(l => selectNeighbours(l)).toArray
      } else {
        minorityIndex.par.map(l => selectNeighboursHVDM(l)).toArray
      }

      selectedNeighbours.flatten.distinct.foreach(e => indexA2 += e)
      j += 1
    }

    val finalIndex: Array[Int] = classesToWorkWith.indices.diff(indexA1.toList ++ indexA2.distinct).toArray
    val finishTime: Long = System.nanoTime()

    if (verbose) {
      val newCounter: Map[Any, Int] = (finalIndex map classesToWorkWith).groupBy(identity).mapValues(_.length)
      println("ORIGINAL SIZE: %d".format(dataToWorkWith.length))
      println("NEW DATA SIZE: %d".format(finalIndex.length))
      println("REDUCTION PERCENTAGE: %s".format(100 - (finalIndex.length.toFloat / dataToWorkWith.length) * 100))
      println("ORIGINAL IMBALANCED RATIO: %s".format(imbalancedRatio(counter, untouchableClass)))
      println("NEW IMBALANCED RATIO: %s".format(imbalancedRatio(newCounter, untouchableClass)))
      println("TOTAL ELAPSED TIME: %s".format(nanoTimeToString(finishTime - initTime)))
    }

    new Data(finalIndex map data.x, finalIndex map data.y, Some(finalIndex), data.fileInfo)
  }
}