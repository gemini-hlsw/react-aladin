// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package react.aladin

// just a linking check
class AladinSuite extends munit.FunSuite {
  test("linking") {
    A.aladin("abc", null)
  }
}
