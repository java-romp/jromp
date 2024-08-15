package jromp.parallel;

import jromp.Constants;
import jromp.parallel.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UtilsTest {
    @Test
    void testCheckThreads() {
        assertThat(Utils.checkThreads(1)).isEqualTo(1);
        assertThat(Utils.checkThreads(2)).isEqualTo(2);

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        assertThat(Utils.checkThreads(availableProcessors)).isEqualTo(availableProcessors);
    }

    @Test
    void testCheckThreadsZero() {
        assertThatThrownBy(() -> Utils.checkThreads(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number of threads must be greater than 0.");
    }

    @Test
    void testCheckThreadsNegative() {
        assertThatThrownBy(() -> Utils.checkThreads(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number of threads must be greater than 0.");
    }

    @Test
    void testCheckThreadsTooMany() {
        int availableProcessors = Constants.MAX_THREADS;
        assertThat(Utils.checkThreads(availableProcessors + 10)).isEqualTo(availableProcessors);
    }

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
