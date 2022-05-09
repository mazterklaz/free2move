package config

import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class AppConfig(customerPath: String,
                     itemsPath: String,
                     ordersPath: String,
                     productsPath: String
                    )

object AppConfig{
  def apply(): AppConfig = ConfigSource.default.load[AppConfig]
    .fold(
      { failure ⇒ throw new Exception(failure.prettyPrint()) },
      {
        identity
      }
    )
}
