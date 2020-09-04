// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

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
