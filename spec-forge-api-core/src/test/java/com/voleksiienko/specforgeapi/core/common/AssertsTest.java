package com.voleksiienko.specforgeapi.core.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AssertsTest {

    @Nested
    class IsEmpty {

        @Test
        void shouldReturnTrueForNull() {
            assertThat(Asserts.isEmpty((Collection<?>) null)).isTrue();
        }

        @Test
        void shouldReturnTrueForEmptyCollection() {
            assertThat(Asserts.isEmpty(List.of())).isTrue();
        }

        @Test
        void shouldReturnFalseForPopulatedCollection() {
            assertThat(Asserts.isEmpty(List.of("item"))).isFalse();
        }
    }

    @Nested
    class IsNotEmpty {

        @Test
        void shouldReturnTrueForPopulatedCollection() {
            assertThat(Asserts.isNotEmpty(List.of("item"))).isTrue();
        }

        @Test
        void shouldReturnFalseForNull() {
            assertThat(Asserts.isNotEmpty((Collection<?>) null)).isFalse();
        }

        @Test
        void shouldReturnFalseForEmptyCollection() {
            assertThat(Asserts.isNotEmpty(List.of())).isFalse();
        }
    }

    @Nested
    class MapIsEmpty {

        @Test
        void shouldReturnTrueForNull() {
            assertThat(Asserts.isEmpty((Map<?, ?>) null)).isTrue();
        }

        @Test
        void shouldReturnTrueForEmptyMap() {
            assertThat(Asserts.isEmpty(Map.of())).isTrue();
        }

        @Test
        void shouldReturnFalseForPopulatedMap() {
            assertThat(Asserts.isEmpty(Map.of("key", "value"))).isFalse();
        }
    }

    @Nested
    class MapIsNotEmpty {

        @Test
        void shouldReturnTrueForPopulatedMap() {
            assertThat(Asserts.isNotEmpty(Map.of("item", "item"))).isTrue();
        }

        @Test
        void shouldReturnFalseForNull() {
            assertThat(Asserts.isNotEmpty((Map<?, ?>) null)).isFalse();
        }

        @Test
        void shouldReturnFalseForEmptyMap() {
            assertThat(Asserts.isNotEmpty(Map.of())).isFalse();
        }
    }

    @Nested
    class IsBlank {

        @Test
        void shouldReturnTrueForNull() {
            assertThat(Asserts.isBlank(null)).isTrue();
        }

        @Test
        void shouldReturnTrueForEmptyString() {
            assertThat(Asserts.isBlank("")).isTrue();
        }

        @Test
        void shouldReturnTrueForWhitespace() {
            assertThat(Asserts.isBlank("   ")).isTrue();
        }

        @Test
        void shouldReturnFalseForText() {
            assertThat(Asserts.isBlank("text")).isFalse();
        }
    }

    @Nested
    class IsNotBlank {

        @Test
        void shouldReturnTrueForText() {
            assertThat(Asserts.isNotBlank("text")).isTrue();
        }

        @Test
        void shouldReturnFalseForNull() {
            assertThat(Asserts.isNotBlank(null)).isFalse();
        }

        @Test
        void shouldReturnFalseForEmptyString() {
            assertThat(Asserts.isNotBlank("")).isFalse();
        }

        @Test
        void shouldReturnFalseForWhitespace() {
            assertThat(Asserts.isNotBlank("   ")).isFalse();
        }
    }

    @Nested
    class RequireNotBlank {

        @Test
        void shouldDoNothingWhenStringIsNotBlank() {
            Asserts.requireNotBlank("valid", "Error message");
        }

        @Test
        void shouldThrowExceptionWhenStringIsBlank() {
            String message = "String cannot be blank";

            assertThatThrownBy(() -> Asserts.requireNotBlank("  ", message))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(message);
        }
    }
}
