package jromp;

import jromp.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtilsTest {
    @Test
    void testIsMaster() {
        assertThat(Utils.isMaster(0)).isTrue();
        assertThat(Utils.isMaster(1)).isFalse();
    }

    @Test
    void testGetWTime() {
        assertThat(Utils.getWTime()).isGreaterThan(0.0);
    }
}
