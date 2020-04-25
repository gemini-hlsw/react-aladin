package react.aladin

import utest._

// just a linking check
object AladinSuite extends TestSuite {
  val tests = Tests {
    test("linking") {
      A.aladin("abc", null)
    }
  }
}
