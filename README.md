# SOUL

### Scala Oversampling and Undersampling Library

Included algorithms for oversampling:

* **Random Oversampling.** Original paper: "A study of the behavior of several methods for balancing machine learning training data" by Batista, Gustavo EAPA and Prati, Ronaldo C and Monard, Maria Carolina.

* **SMOTE.** Original paper: "SMOTE: Synthetic Minority Over-sampling Technique" by Nitesh V. Chawla, Kevin W. Bowyer, Lawrence O. Hall and W. Philip Kegelmeyer.

* **SMOTE + ENN.** Original paper: "A Study of the Behavior of Several Methods for Balancing Machine Learning Training Data" by Gustavo E. A. P. A. Batista, Ronaldo C. Prati and Maria Carolina Monard.

* **SMOTE + TL.** Original paper: "A Study of the Behavior of Several Methods for Balancing Machine Learning Training Data" by Gustavo E. A. P. A. Batista, Ronaldo C. Prati and Maria Carolina Monard.

* **Borderline-SMOTE.** Original paper: "Borderline-SMOTE: A New Over-Sampling Method in Imbalanced Data Sets Learning." by Hui Han, Wen-Yuan Wang, and Bing-Huan Mao.

* **Adasyn.** Original paper: "ADASYN: Adaptive Synthetic Sampling Approach for Imbalanced Learning" by Haibo He, Yang Bai, Edwardo A. Garcia, and Shutao Li.

* **Adoms.** Original paper: "The Generation Mechanism of Synthetic Minority Class Examples" by Sheng TANG and Si-ping CHEN.

* **SafeLevel-SMOTE.** Original paper: "Safe-Level-SMOTE: Safe-Level-Synthetic Minority Over-Sampling TEchnique for Handling the Class Imbalanced Problem" by Chumphol Bunkhumpornpat, Krung Sinapiromsaran, and Chidchanok Lursinsap.

* **Spider2.** Original paper: "Learning from Imbalanced Data in Presence of Noisy and Borderline Examples" by Krystyna Napiera la, Jerzy Stefanowski and Szymon Wilk.

* **DBSMOTE.** Original paper: "DBSMOTE: Density-Based Synthetic Minority Over-sampling Technique" by Chumphol Bunkhumpornpat,  Krung Sinapiromsaran and Chidchanok Lursinsap.

* **SMOTE-RSB.** Original paper: "kNN Approach to Unbalanced Data Distribution: SMOTE-RSB: a hybrid preprocessing approach based on oversampling and undersampling for high imbalanced data-sets using SMOTE and rough sets theory" by Enislay Ramentol, Yailé Caballero, Rafael Bello and Francisco Herrera.

* **MWMOTE.** Original paper: "MWMOTE—Majority Weighted Minority Oversampling Technique for Imbalanced Data Set Learning" by Sukarna Barua, Md. Monirul Islam, Xin Yao, Fellow, IEEE, and Kazuyuki Muras.

* **MDO.** Original paper: "To combat multi-class imbalanced problems by means of over-sampling and boosting techniques" by Lida Adbi and Sattar Hashemi.

Included algorithms for undersampling:

* **Random Undersampling.** Original paper: "A study of the behavior of several methods for balancing machine learning training data" by Batista, Gustavo EAPA and Prati, Ronaldo C and Monard, Maria Carolina.

* **Condensed Nearest Neighbor decision rule.** Original paper: "The Condensed Nearest Neighbor Rule" by P. Hart.

* **Edited Nearest Neighbour rule.** Original paper: "Asymptotic Properties of Nearest Neighbor Rules Using Edited Data" by Dennis L. Wilson.

* **Tomek Link.** Original paper: "Two Modifications of CNN" by Ivan Tomek.

* **One-Side Selection.** Original paper: "Addressing the Curse of Imbalanced Training Sets: One-Side Selection" by Miroslav Kubat and Stan Matwin.

* **Neighbourhood Cleaning Rule.** Original paper: "Improving Identification of Difficult Small Classes by Balancing Class Distribution" by J. Laurikkala.

* **NearMiss.** Original paper: "kNN Approach to Unbalanced Data Distribution: A Case Study involving Information Extraction" by Jianping Zhang and Inderjeet Mani.

* **Class Purity Maximization algorithm.** Original paper: "An Unsupervised Learning Approach to Resolving the Data Imbalanced Issue in Supervised Learning Problems in Functional Genomics" by Kihoon Yoon and Stephen Kwek.

* **Undersampling Based on Clustering.** Original paper: "Under-Sampling Approaches for Improving Prediction of the Minority Class in an Imbalanced Dataset" by Show-Jane Yen and Yue-Shi Lee.

* **Balance Cascade.** Original paper: "Exploratory Undersampling for Class-Imbalance Learning" by Xu-Ying Liu, Jianxin Wu and Zhi-Hua Zhou.

* **Easy Ensemble.** Original paper: "Exploratory Undersampling for Class-Imbalance Learning" by Xu-Ying Liu, Jianxin Wu and Zhi-Hua Zhou.

* **Evolutionary Undersampling.** Original paper: "Evolutionary Under-Sampling for Classification with Imbalanced Data Sets: Proposals and Taxonomy" by Salvador Garcia and Francisco Herrera.

* **Instance Hardness Threshold.** Original paper: "An Empirical Study of Instance Hardness" by Michael R. Smith, Tony Martinez and Christophe Giraud-Carrier.

* **ClusterOSS.** Original paper: "ClusterOSS: a new undersampling method for imbalanced learning." by Victor H Barella, Eduardo P Costa and André C. P. L. F. Carvalho.

* **Iterative Instance Adjustment for Imbalanced Domains.** Original paper: "Addressing imbalanced classification with instance generation techniques: IPADE-ID" by Victoria López, Isaac Triguero, Cristóbal J. Carmona, Salvador García and Francisco Herrera.

### How-to use it

If you are going to use this library from another `sbt` project, you just need to clone the original repository, in the root folder of the cloned repository execute `sbt publishLocal` and add the following dependendy to the `build.sbt` file of your project:

```scala
libraryDependencies += "com.github.soul" %% "soul" % "1.0.0"
```

To read a data file you only need to do this:

```scala
import soul.io.Reader
import soul.data.Data

/* Read a csv file or any delimited text file */
val csvData: Data = Reader.readDelimitedText(file = <pathToFile>)
/* Read a WEKA arff file */
val arffData: Data = Reader.readArff(file = <pathToFile>)
```

Now we're going to run an undersampling algorithm:

```scala
import soul.algorithm.undersampling.NCL
import soul.data.Data

val nclCSV = new NCL(csvData)
val resultCSV: Data = nclCSV.compute()

val nclARFF = new NCL(arffData)
val resultARFF: Data = nclARFF.compute()
```

In this example we've used an undersampling algorithm but it's the same for an oversampling one. All the algorithm's parameters have default values so you don't need to specify any of them.

Finally, we only need to save the result to a file: 

```scala
import soul.io.Writer

Writer.writeDelimitedText(file = <pathToFile>, data = resultCSV)
Writer.writeArff(file = <pathToFile>, data = resultARFF)
```

### Experiments

With the objective of showing the capabilities of **SOUL**, we have generated a two dimension synthetic imbalanced dataset with 1,871 instances. Among them, 1,600 instances belong to the majority class and the remaining 271 belongs to the minority class, leading to about a 17% of minority instances in the whole dataset (IR=5.9). The representation of this dataset can be found below, where we may observe a clear overlapping between the classes, as well as a cluster of minority instances in the middle of the majority instances. 

Next, we have used the following parameters of the algorithms to perform an experiment with some relevant oversampling and undersampling approaches:


* **MWMOTE**: *seed*: 0, *N*: 1400, *k1*: 5, *k2*: 5, *k3*: 5, *dist*: euclidean, *normalize*: false, *verbose*: false.

* **SMOTE**: *seed*: 0, *percent*: 500, *k*: 5, *dist*: euclidean, *normalize*: false, *verbose*: false.

* **ADASYN**: *seed*: 0, *d*: 1, *B*: 1, *k*: 5, *dist*: euclidean, *normalize*: false, *verbose*: false.

* **SafeLevelSMOTE**: *seed*: 0, *k*: 5, *dist*: euclidean, *normalize*: false, *verbose*: false.

* **IHTS**: *seed* = 0, *nFolds* = 5, *normalize* = false, *randomData* = false, *verbose* = false

* **IPADE**: *seed* = 0, *iterations* = 100, *strategy* = 1, *randomChoice* = true, *normalize* = false, *randomData* = false, *verbose* = false

* **NCL**: *seed* = 0, *dist* = euclidean, *k* = 3, *threshold* = 0.5, *normalize* = false, *randomData* = false, *verbose* = false

* **SBC**: *seed* = 0, *method* = "NearMiss1", *m* = 1.0, *k* = 3, *numClusters* = 50, *restarts* = 1, *minDispersion* = 0.0001, *maxIterations* = 200, val *dist* = euclidean, *normalize* = false, *randomData* = false, *verbose* = false


![Original](images/original.png)


| ![ADASYN](images/ADASYN.png) | ![SafeLevelSMOTE](images/SafeLevelSMOTE.png) |
| ------------- | ------------- |
![MWMOTE](images/MWMOTE.png) | ![SMOTE](images/SMOTE.png)


| ![IHTS](images/IHTS.png) | ![IPADE](images/IPADE.png) |
| ------------- | ------------- |
![NCL](images/NCL.png) | ![SBC](images/SBC.png)
