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