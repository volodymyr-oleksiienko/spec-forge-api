package com.voleksiienko.specforgeapi.core.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AssertsTest {

    @Nested
    @DisplayName("isEmpty()")
    class IsEmpty {

        @Test
        void shouldReturnTrueForNull() {
            assertThat(Asserts.isEmpty(null)).isTrue();
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
    @DisplayName("isNotEmpty()")
    class IsNotEmpty {

        @Test
        void shouldReturnTrueForPopulatedCollection() {
            assertThat(Asserts.isNotEmpty(List.of("item"))).isTrue();
        }

        @Test
        void shouldReturnFalseForNull() {
            assertThat(Asserts.isNotEmpty(null)).isFalse();
        }

        @Test
        void shouldReturnFalseForEmptyCollection() {
            assertThat(Asserts.isNotEmpty(List.of())).isFalse();
        }
    }

    @Nested
    @DisplayName("isBlank()")
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
    @DisplayName("isNotBlank()")
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
}
