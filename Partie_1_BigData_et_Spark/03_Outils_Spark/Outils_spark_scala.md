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
