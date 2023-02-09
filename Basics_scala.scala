// ############################################################
// Les bases de scala
// ############################################################

// source : https://docs.scala-lang.org/tour/basics.html

// -----------------------------------------------------------------
// Les Expressions 
// -----------------------------------------------------------------
// Vous pouvez faire des calculs dans scala
1 + 1
2 * 6
76 - 14

// la fonction print est println()
println(1) // 1
println(1 + 1) // 2
println("Hello!") // Hello!
println("Hello," + " world!") // Hello, world!

// -----------------------------------------------------------------
// Les valeurs dans scala - Values
// -----------------------------------------------------------------
val x = 1 + 1
println(x) // 2

// Les valeurs ne peuvent pas être réaffectées :
x = 3 // Cela ne se compile pas.

// Le type d'une valeur peut être omis et déduit, ou il peut être explicitement indiqué :
val x: Int = 1 + 1

// -----------------------------------------------------------------
// Les Variables dans scala 
// -----------------------------------------------------------------
// Les variables sont comme des valeurs, sauf que vous pouvez les réaffecter. Vous pouvez définir une variable avec le mot-clé var.
var x = 1 + 1
x = 3 // This compiles because "x" is declared with the "var" keyword.
println(x * x) // 9

// Comme pour les valeurs, le type d'une variable peut être omis et déduit, ou bien il peut être explicitement indiqué :
var x: Int = 1 + 1

// -----------------------------------------------------------------
// Les blocs - Blocks
// -----------------------------------------------------------------
// Vous pouvez combiner des expressions en les entourant de {}. Nous appelons cela un bloc. Le résultat de la dernière expression du bloc est également le résultat de l'ensemble du bloc :
println({
  val x = 1 + 1
  x + 1
}) // 3

// -----------------------------------------------------------------
// Les fonction - Functions
// -----------------------------------------------------------------
// Les fonctions sont des expressions qui ont des paramètres et prennent des arguments.
// Vous pouvez définir une fonction anonyme (c'est-à-dire une fonction qui n'a pas de nom) qui renvoie un nombre entier donné plus un :
(x: Int) => x + 1


// A gauche de => se trouve une liste de paramètres. A droite, une expression impliquant les paramètres.
// Vous pouvez également nommer les fonctions :
val addOne = (x: Int) => x + 1
println(addOne(1)) // 2

// Une fonction peut avoir plusieurs paramètres :
val add = (x: Int, y: Int) => x + y
println(add(1, 2)) // 3


// Ou il peut ne pas avoir de paramètres du tout :
val getTheAnswer = () => 42
println(getTheAnswer()) // 42

// -----------------------------------------------------------------
// Les méthodes dans scala - Methods
// -----------------------------------------------------------------
// Les méthodes ont un aspect et un comportement très similaires aux fonctions, mais il existe quelques différences essentielles entre elles. 
// Les méthodes sont définies avec le mot-clé def. def est suivi d'un nom, d'une liste de paramètres, d'un type de retour et d'un corps :
def add(x: Int, y: Int): Int = x + y
println(add(1, 2)) // 3


// Une méthode peut prendre plusieurs listes de paramètres :
def addThenMultiply(x: Int, y: Int)(multiplier: Int): Int = (x + y) * multiplier
println(addThenMultiply(1, 2)(3)) // 9


// ou pas de paramètres du tout:
def name: String = System.getProperty("user.name")
println("Hello, " + name + "!")


// Les méthodes peuvent avoir plusieurs lignes:
// # scala 2 : la fonction return est optionnelle, les accolades sont obligatoires (vs scala 3)
def getSquareString(input: Double): String = {
  val square = input * input
  return square.toString
}
println(getSquareString(2.5)) // 6.25

// # scala 3
def getSquareString(input: Double): String =
  val square = input * input
  square.toString

println(getSquareString(2.5)) // 6.25

// -----------------------------------------------------------------
// Les Classes dans scala
// -----------------------------------------------------------------

