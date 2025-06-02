package fix

import scalafix.v1._

import scala.meta._

final class UncatYourProgram extends SemanticRule("UncatYourProgram") {
  override val description: String =
    "Semantic rule to rewrite TypeLevel libraries syntax to stdlib"

  private val foldCatcher = SymbolMatcher.normalized("mouse.BooleanOps.fold")
  private val ignoreCatcher =
    SymbolMatcher.normalized("mouse.MouseFunctions.ignore")
  private val eitherIdRightCatcher =
    SymbolMatcher.normalized("cats/syntax.EitherIdOps#asRight")
  private val eitherIdLeftCatcher =
    SymbolMatcher.normalized("cats/syntax.EitherIdOps#asLeft")
  private val eitherLeftMapCatcher =
    SymbolMatcher.normalized("cats/syntax.EitherOps#leftMap")

  override def fix(implicit doc: SemanticDocument): Patch = {
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
      case eitherIdRightCatcher(
            x @ Term.Select(extendedTerm, Term.Name("asRight"))
          ) =>
        Patch.replaceTree(x, s"Right(${extendedTerm.syntax})")
      case eitherIdLeftCatcher(
            x @ Term.Select(extendedTerm, Term.Name("asLeft"))
          ) =>
        Patch.replaceTree(x, s"Left(${extendedTerm.syntax})")
      case eitherLeftMapCatcher(
            x @ Term.Apply(
              Term.Select(extendedTerm, Term.Name("leftMap")),
              func :: Nil
            )
          ) =>
        Patch.replaceTree(x, s"${extendedTerm.syntax}.left.map(${func.syntax})")
      case catsEitherSyntaxImport @ Import(
            Importer(
              Term.Select(
                Term.Select(Term.Name("cats"), Term.Name("syntax")),
                Term.Name("either")
              ),
              _
            ) :: Nil
          ) =>
        Patch.removeTokens(catsEitherSyntaxImport.tokens)
    }.asPatch
  }

}
