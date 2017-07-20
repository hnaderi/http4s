package org.http4s
package parser

import org.http4s.internal.parboiled2._
import org.http4s.headers.`Strict-Transport-Security`
import scala.concurrent.duration._
import org.http4s.internal.parboiled2.support.{HNil, ::}

private[parser] trait StrictTransportSecurityHeader {
  def STRICT_TRANSPORT_SECURITY(value: String): ParseResult[`Strict-Transport-Security`] =
    StrictTransportSecurityParser(value).parse

  private case class StrictTransportSecurityParser(override val input: ParserInput) extends Http4sHeaderParser[`Strict-Transport-Security`](input) {
    def entry: Rule1[`Strict-Transport-Security`] = rule {
      maxAge ~ zeroOrMore(";" ~ OptWS ~ stsAttributes) ~ EOI
    }

    def maxAge: Rule1[`Strict-Transport-Security`] = rule {
      "max-age=" ~ Digits ~> { (age: String) => `Strict-Transport-Security`(maxAge = age.toLong.seconds, includeSubDomains = false, preload = false) }
    }

    def stsAttributes: Rule[`Strict-Transport-Security`::HNil, `Strict-Transport-Security`::HNil] = rule {
      capture("includeSubDomains") ~> { (sts: `Strict-Transport-Security`, _: String) => sts.copy(includeSubDomains = true) } |
      capture("preload")           ~> { (sts: `Strict-Transport-Security`, _: String) => sts.copy(preload = true) }
    }
  }
}
