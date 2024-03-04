## Outils Spark - exemples en scala

Dans cette section, nous aborderons les sujets suivants :

- Exécution d'applications de production avec `spark-submit`
- Datasets : APIs de type sécurisé pour les données structurées
- Le streaming avec Spark
- Apprentissage automatique et analyses avancées
- Resilient Distributed Datasets (RDD)
- SparkR
- L'écosystème des packages tiers

### Exécution d'applications de production avec `spark-submit`
Vous avez créé votre fichier JAR (votre package ou votre application, peu importe le langage utilisé). Pour lancer votre application sur un cluster, vous devez utiliser `spark-submit`.

Pour lancer un `spark-submit`, vous avez besoin d'au moins 4 paramètres :

- `--class`: le nom de la classe de votre application
- `--master`: le cluster sur lequel vous allez envoyer votre application pour tourner
- `.jar`: le JAR (compilé) contenant votre package (votre application)
- `arguments` (optionnel) : les arguments pour votre application (par exemple : la configuration)

Voici un exemple à lancer sur votre machine en local :

```bash
./spark-submit --class org.apache.spark.examples.SparkPi --master local[*] ./examples/jars/spark-examples_2.11-2.2.0.jar 10
```
Cet exemple d'application calcule les décimales de pi avec une certaine précision. Nous avons spécifié à spark-submit que nous voulons exécuter localement, en précisant la classe et le JAR à exécuter, ainsi que des arguments en ligne de commande pour cette classe.

```
ℹ️ En modifiant l'argument master de spark-submit, nous pouvons également soumettre la même application à un cluster exécutant le gestionnaire de cluster autonome de Spark, Mesos ou YARN.
```

### Datasets : APIs de type sécurisé pour les données structurées
Les Datasets offrent une API de type sécurisé pour manipuler des données structurées dans Spark. 
Ils permettent de définir le schéma des données en utilisant des classes Java ou Scala, offrant ainsi une manipulation plus sécurisée des données. 
Avec les Datasets, les opérations de transformation et de manipulation des données sont vérifiées à la compilation, ce qui permet de détecter les erreurs de type à l'avance. De plus, les Datasets sont optimisés pour des performances élevées, ce qui les rend idéaux pour le traitement de gros volumes de données dans des environnements distribués.

Voici un petit exemple montrant comment vous pouvez utiliser à la fois des fonctions de type sécurisé et des expressions SQL similaires à celles des DataFrames pour écrire rapidement la logique métier :

```scala
case class Flight(DEST_COUNTRY_NAME: String, ORIGIN_COUNTRY_NAME: String, count: BigInt)
val flightsDF = spark.read.parquet("D:/data/flight-data/parquet/2010-summary.parquet/")
val flights = flightsDF.as[Flight]
```
#### - Opérations sur le Dataset flights :

- Filtre les vols pour exclure ceux dont le pays d'origine est le Canada.
- Mappe chaque ligne de vol telle quelle et prend les 5 premières lignes.
```scala
flights.filter(flight_row => flight_row.ORIGIN_COUNTRY_NAME != "Canada")
      .map(flight_row => flight_row)
      .take(5)
```
#### - Opérations sur le Dataset flights :

- Prend les 5 premières lignes de vols.
- Filtre ces lignes pour exclure celles dont le pays d'origine est le Canada.
- Mappe chaque ligne de vol pour créer un nouvel objet Flight avec le pays de destination, le pays d'origine et le nombre de vols augmenté de 5 pour chacun.
```scala
flights.take(5)
      .filter(flight_row => flight_row.ORIGIN_COUNTRY_NAME != "Canada")
      .map(fr => Flight(fr.DEST_COUNTRY_NAME, fr.ORIGIN_COUNTRY_NAME, fr.count + 5))
```
### Le streaming avec Spark
Structured **Streaming** est une API de haut niveau pour le traitement de flux, intégrée dans Spark 3, permettant d'exécuter des opérations similaires à celles du mode **batch** en mode streaming, offrant une réduction de la latence et une transformation incrémentielle des données avec peu de changements de code.

Nous allons explorer un exemple simple de Structured Streaming en utilisant un jeu de données de vente au détail, avec des dates et heures spécifiques, pour simuler des données produites de manière régulière par des magasins, en créant un DataFrame statique et un schéma associé.

```scala
// Lecture des données statiques depuis un fichier CSV
val staticDataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("D:/data/retail-data/by-day/*.csv")

// Afficher les 10 premières lignes
staticDataFrame.show(10)

// Création d'une vue temporaire pour le DataFrame statique
staticDataFrame.createOrReplaceTempView("retail_data")
val staticSchema = staticDataFrame.schema

// Agrégation des données par heure de vente pour identifier les jours où un client dépense le plus
import org.apache.spark.sql.functions.{window, column, desc, col}
staticDataFrame.selectExpr("CustomerId", "(UnitPrice * Quantity) as total_cost",
"InvoiceDate").groupBy(col("CustomerId"), window(col("InvoiceDate"), "1 day")).sum("total_cost").show(5)
```
Vous aurez quelque chose comme ça :
```scala
// +----------+--------------------+-----------------+
// |CustomerId|              window|  sum(total_cost)|
// +----------+--------------------+-----------------+
// |   16057.0|{2011-12-05 01:00...|            -37.6|
// |   14126.0|{2011-11-29 01:00...|643.6300000000001|
// |   13500.0|{2011-11-16 01:00...|497.9700000000001|
// |   17160.0|{2011-11-08 01:00...|516.8499999999999|
// |   15608.0|{2011-11-11 01:00...|            122.4|
// +----------+--------------------+-----------------+
```
Maintenant, passant en mode `streaming` pour voir ce qui change par rapport au mode batch (dataframe).
Commençant par réduire le nombre de partitions pour le shuffle à 5 (Par défaut, Spark utilise un nombre de partitions déterminé par la configuration spark.sql.shuffle.partitions, qui est souvent ajusté automatiquement en fonction des ressources disponibles, mais est généralement égal au nombre de cœurs sur la machine en mode local.
)
```scala
// Configuration du nombre de partitions pour le shuffle à 5 pour une exécution en mode local
spark.conf.set("spark.sql.shuffle.partitions", "5")

// Lecture des données en streaming à partir de plusieurs fichiers CSV
val streamingDataFrame = spark.readStream.schema(staticSchema).option("maxFilesPerTrigger", 1).format("csv").option("header", "true").load("D:/data/retail-data/by-day/*.csv")

// Agrégation des données par heure de vente pour le streaming (même action que sur le dataframe)
val purchaseByCustomerPerHour = streamingDataFrame.selectExpr("CustomerId",
"(UnitPrice * Quantity) as total_cost","InvoiceDate").groupBy($"CustomerId", window($"InvoiceDate", "1 day")).sum("total_cost")

// Écriture des résultats en streaming dans une table en mémoire
purchaseByCustomerPerHour
.writeStream.format("memory") // stockage dans une table en mémoire
.queryName("customer_purchases") // nom de la table en mémoire
.outputMode("complete") // mode de sortie complet
.start()

// Exécution d'une requête SQL pour afficher les résultats en temps réél (mis à jour au fur à mesure de la lécture des fichiers)
spark.sql("""
SELECT *
FROM customer_purchases
ORDER BY `sum(total_cost)` DESC
""").show(5)

// Écriture des résultats en streaming dans la console : pas recommandé en production
purchaseByCustomerPerHour.writeStream
.format("console")
.queryName("customer_purchases_2")
.outputMode("complete")
.start()
```
Vous aurez un tableau comme ça (qui se met à jour à chaque batch)
```scala
// -------------------------------------------
// Batch: 11
// -------------------------------------------
// +----------+--------------------+-------------------+
// |CustomerId|              window|    sum(total_cost)|
// +----------+--------------------+-------------------+
// |   17576.0|{2010-12-13 01:00...| 177.35000000000002|
// |   15039.0|{2010-12-14 01:00...|  706.2500000000002|
// |   16250.0|{2010-12-01 01:00...|             226.14|
// |   14594.0|{2010-12-01 01:00...| 254.99999999999997|
// |   15899.0|{2010-12-06 01:00...|              56.25|
// |   14850.0|{2010-12-07 01:00...|              -47.6|
// |   17220.0|{2010-12-10 01:00...| 317.50000000000006|
// |   14865.0|{2010-12-02 01:00...|               37.2|
// |   14800.0|{2010-12-05 01:00...|  555.8399999999999|
// |   14256.0|{2010-12-10 01:00...|  523.8599999999999|
// |   12434.0|{2010-12-14 01:00...|-27.749999999999996|
// |   18041.0|{2010-12-02 01:00...|  428.9399999999999|
// |   16565.0|{2010-12-10 01:00...|              173.7|
// |   17949.0|{2010-12-03 01:00...|             1314.0|
// |   17675.0|{2010-12-07 01:00...|  541.5200000000001|
// |   18118.0|{2010-12-10 01:00...| 132.64999999999998|
// |   12471.0|{2010-12-10 01:00...|            2360.41|
// |   13329.0|{2010-12-14 01:00...|-13.200000000000001|
// |   15555.0|{2010-12-05 01:00...|             198.43|
// |   18074.0|{2010-12-01 01:00...|              489.6|
// +----------+--------------------+-------------------+
```
Volà!, vous avez lancer votre premier job en streaming (lecture de plusieurs fichiers csv).

### Apprentissage automatique et analyses avancées
Un autre aspect populaire de Spark est sa capacité à effectuer un apprentissage automatique (machine Learning) à grande échelle avec une bibliothèque intégrée d'algorithmes d'apprentissage 
automatique appelée *`MLlib`*. MLlib permet la prétraitement, la manipulation, l'entraînement des modèles et la réalisation de prédictions à grande échelle sur les données. 
Vous pouvez même utiliser des modèles entraînés dans MLlib pour effectuer des prédictions dans Structured Streaming. Spark fournit une API d'apprentissage automatique sophistiquée pour 
effectuer une variété de tâches d'apprentissage automatique, de la classification à la régression, en passant par le clustering et l'apprentissage profond (deep learning). 
Pour illustrer cette fonctionnalité, nous effectuerons un simple clustering sur nos données à l'aide d'un algorithme standard appelé K-means.

On va utiliser le meme dataset sur le retail : 
```scala
staticDataFrame.printSchema()
// root
// |-- InvoiceNo: string (nullable = true)
// |-- StockCode: string (nullable = true)
// |-- Description: string (nullable = true)
// |-- Quantity: integer (nullable = true)
// |-- InvoiceDate: timestamp (nullable = true)
// |-- UnitPrice: double (nullable = true)
// |-- CustomerID: double (nullable = true)
// |-- Country: string (nullable = true)
```

Les algorithmes d'apprentissage automatique dans MLlib exigent que les données soient représentées sous forme de valeurs numériques. Nos données actuelles sont représentées par une variété de types différents, notamment des horodatages, des entiers et des chaînes de caractères. Par conséquent, nous devons transformer ces données en une représentation numérique. Dans ce cas, nous utiliserons plusieurs transformations de DataFrame pour manipuler nos données de date :
```scala
import org.apache.spark.sql.functions.date_format
val prepDataFrame = staticDataFrame.na.fill(0).withColumn("day_of_week", date_format($"InvoiceDate", "EEEE"))
prepDataFrame.show()
```

Dans cet exemple, nous divisons les données en un ensemble d'entraînement et un ensemble de test en utilisant la date à laquelle un certain achat a eu lieu. Nous utilisons la fonction where pour filtrer les données en fonction de la date de la facture.
```scala
// Séparation des données en ensembles d'entraînement et de test
val trainDataFrame = prepDataFrame.where("InvoiceDate < '2011-07-01'")
val testDataFrame = prepDataFrame.where("InvoiceDate >= '2011-07-01'")
```
Nous verrons que cela divise approximativement notre ensemble de données en deux (Bien que cela puisse ne pas être la division optimale pour notre entraînement et notre test):
```scala
trainDataFrame.count()
testDataFrame.count()
```