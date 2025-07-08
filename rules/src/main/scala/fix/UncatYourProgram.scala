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
  private val zioMapCatcher = SymbolMatcher.normalized("zio.ZIO#map")
  private val invalidNecCatcher =
    SymbolMatcher.normalized("cats/syntax/ValidatedIdOpsBinCompat0#invalidNec")
  private val validNecCatcher =
    SymbolMatcher.normalized("cats/syntax/ValidatedIdOpsBinCompat0#validNec")
  private val pureCatcher =
    SymbolMatcher.normalized("cats/syntax/ApplicativeIdOps#pure")

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
      case zioApplyLeft @ Term.Apply(
            Term.Select(Term.Name("ZIO"), Term.Name("succeed")),
            Term.Apply(Term.Name("Left"), leftContents :: Nil) :: Nil
          ) =>
        Patch.replaceTree(zioApplyLeft, s"ZIO.left(${leftContents.syntax})")
      case zioApplyRight @ Term.Apply(
            Term.Select(Term.Name("ZIO"), Term.Name("succeed")),
            Term.Apply(Term.Name("Right"), rightContents :: Nil) :: Nil
          ) =>
        Patch.replaceTree(zioApplyRight, s"ZIO.right(${rightContents.syntax})")
      case zioMapCatcher(
            x @ Term.Apply(
              Term.Select(mappedTerm, Term.Name("map")),
              Term.Select(Term.Name("Left"), Term.Name("apply")) :: Nil
            )
          ) =>
        Patch.replaceTree(x, s"${mappedTerm.syntax}.asLeft")
      case zioMapCatcher(
            x @ Term.Apply(
              Term.Select(mappedTerm, Term.Name("map")),
              Term.Select(Term.Name("Right"), Term.Name("apply")) :: Nil
            )
          ) =>
        Patch.replaceTree(x, s"${mappedTerm.syntax}.asRight")
      case zioMapCatcher(
            x @ Term.Apply(
              Term.Select(mappedTerm, Term.Name("map")),
              Term.AnonymousFunction(
                Term.Apply(Term.Name("Right"), (_: Term.Placeholder) :: Nil)
              ) :: Nil
            )
          ) =>
        Patch.replaceTree(x, s"${mappedTerm.syntax}.asRight")
      case zioMapCatcher(
            x @ Term.Apply(
              Term.Select(mappedTerm, Term.Name("map")),
              Term.AnonymousFunction(
                Term.Apply(Term.Name("Left"), (_: Term.Placeholder) :: Nil)
              ) :: Nil
            )
          ) =>
        Patch.replaceTree(x, s"${mappedTerm.syntax}.asLeft")
      case invalidNecCatcher(
            x @ Term.ApplyType(
              Term.Select(extendedTerm, Term.Name("invalidNec")),
              Type.Name(_) :: Nil
            )
          ) =>
        Patch.replaceTree(x, s"Validated.invalidNec(${extendedTerm.syntax})")
      case validNecCatcher(
            x @ Term.ApplyType(
              Term.Select(extendedTerm, Term.Name("validNec")),
              Type.Name(_) :: Nil
            )
          ) =>
        Patch.replaceTree(x, s"Validated.validNec(${extendedTerm.syntax})")
      case catsValidatedSyntaxImport @ Import(
            Importer(
              Term.Select(
                Term.Select(Term.Name("cats"), Term.Name("syntax")),
                Term.Name("validated")
              ),
              _
            ) :: Nil
          ) =>
        Patch.replaceTree(
          catsValidatedSyntaxImport,
          "import cats.data.Validated"
        )
      case pureCatcher(
            x @ Term.ApplyType(
              Term.Select(extendedTerm, Term.Name("pure")),
              Type.Name(typeName) :: Nil
            )
          ) =>
        Patch.replaceTree(
          x,
          s"Applicative[$typeName].pure(${extendedTerm.syntax})"
        )
      case catsApplicativeSyntaxImport @ Import(
            Importer(
              Term.Select(
                Term.Select(Term.Name("cats"), Term.Name("syntax")),
                Term.Name("applicative")
              ),
              _
            ) :: Nil
          ) =>
        Patch.replaceTree(
          catsApplicativeSyntaxImport,
          "import cats.Applicative"
        )
    }.asPatch
  }

}
