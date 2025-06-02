package fix

import scalafix.v1._

import scala.meta._

final class UncatYourProgram extends SemanticRule("UncatYourProgram") {
  override val description: String =
    "Semantic rule to rewrite TypeLevel libraries syntax to stdlib"

  override def fix(implicit doc: SemanticDocument): Patch = {
    val foldCatcher = SymbolMatcher.normalized("mouse.BooleanOps.fold")
    val ignoreCatcher = SymbolMatcher.normalized("mouse.MouseFunctions.ignore")
    doc.tree.collect {
      case foldCatcher(
            x @ Term.Apply(
              Term.Select(extendedTerm, Term.Name("fold")),
              onConditionTerms
            )
          ) =>
        if (onConditionTerms.size < 2) Patch.empty
        else
          Patch.replaceTree(
            x,
            s"(if (${extendedTerm.syntax}) ${onConditionTerms.head.syntax} else ${onConditionTerms.last.syntax})"
          )
      case booleanSyntaxImport @ Import(
            Importer(
              Term.Select(Term.Name("mouse"), Term.Name("boolean")),
              _
            ) :: Nil
          ) =>
        Patch.removeTokens(booleanSyntaxImport.tokens)
      case ignoreCatcher(
            x @ Term.Apply(Term.Name("ignore"), sideEffect :: Nil)
          ) =>
        Patch.replaceTree(x, s"(${sideEffect.syntax}): Unit")
      case ignoreImport @ Import(
            Importer(
              Term.Name("mouse"),
              Importee.Name(Name.Indeterminate("ignore")) :: Nil
            ) :: Nil
          ) =>
        Patch.removeTokens(ignoreImport.tokens)
    }.asPatch
  }

}
