
package com.kent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class FooTest {


  @Test
  void testFoo() {
    String str = StringUtils.substringAfter("foobar", "obar");
    assertEquals(str, "");
    assertEquals(1, 1);
  }

  @Test
  void testBar() {
    List<String> list = new ArrayList<String>();
    list.add("foo");
    assertEquals(list.size() , 1, "size should be 1");
    list.add("bar");
    assertEquals(list.size() , 2, "size should be 1");
  }

}
