package kotlinslang.algebra


import kotlinslang.control.failure
import kotlinslang.control.optionOf
import kotlinslang.control.success
import kotlinslang.foldLeft
import kotlinslang.foldLeftMap
import kotlinslang.foldMap
import kotlinslang.foldRight
import kotlinslang.foldRightMap
import kotlinslang.function.identity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test
import java.util.NoSuchElementException

/**
 * Series Test for Algebra Interfaces such as Functor, Monad and Monoid.
 *
 * @author Deny Prasetyo.
 */

class AlgebraTest {

    @Test
    fun shouldCombineMonoids() {
        val endo: Monoid<(Int) -> Int> = endoMonoid()
        val after = { i: Int -> i + 1 }
        val before = { i: Int -> i * 2 }
        assertThat(endo.combine(after, before)(2)).isEqualTo(5)
    }

    @Test
    fun monoidOfShouldCreate() {
        val monoid = monoidOf(zero = 10, combiner = { a: Int, b: Int -> a * b })
        assertThat(monoid.zero()).isEqualTo(10)
        assertThat(monoid.combine(2, 4)).isEqualTo(8)
    }

    @Test
    fun monadLiftShouldCreateSome() {
        val liftedMonad = monadLift<Int, Double> { l -> l + 4 * 0.1 }
        val monadOption = optionOf(20)
        val liftedOption = liftedMonad(monadOption)
        liftedOption.forEach { o -> assertThat(o).isEqualTo(20.4) };
    }

    @Test
    fun monadLiftShouldCreateNone() {
        val liftedMonad = monadLift<Int, Double> { l -> l + 4 * 0.1 }
        val liftedOption = liftedMonad(optionOf(null))
        liftedOption.forEach { o -> assertThat(o).isNull() };
    }

    @Test
    fun foldLeftBehaveCorrectly() {
        val zero = "zero"
        val value = "hello"
        val expected = "zero-hello"
        val combiner = { a: String, b: String -> "$a-$b" }
        val monoid = monoidOf(zero, combiner)

        val foldable = optionOf(value)
        assertThat(foldable.foldLeft(monoid)).isEqualTo(expected)
        assertThat(foldable.fold(zero, combiner)).isEqualTo(expected)

        val emptyFoldable = optionOf<String>(null)
        assertThat(emptyFoldable.foldLeft(monoid)).isEqualTo(zero)
        assertThat(emptyFoldable.fold(zero, combiner)).isEqualTo(zero)
    }

    @Test
    fun foldRightBehaveCorrectly() {
        val zero = "zero"
        val value = "hello"
        val expected = "hello-zero"
        val combiner = { a: String, b: String -> "$a-$b" }
        val monoid = monoidOf(zero, combiner)

        val foldable = optionOf(value)
        assertThat(foldable.foldRight(zero, combiner)).isEqualTo(expected)
        assertThat(foldable.foldRight(monoid)).isEqualTo(expected)

        val emptyFoldable = optionOf<String>(null)
        assertThat(emptyFoldable.foldRight(zero, combiner)).isEqualTo(zero)
        assertThat(emptyFoldable.foldRight(monoid)).isEqualTo(zero)

    }

    @Test
    fun foldLeftMapBehaveCorrectly() {
        val zero = "zero"
        val value = 42
        val expected = "zero-[42]"
        val combiner = { a: String, b: String -> "$a-$b" }
        val monoid = monoidOf(zero, combiner)

        val foldable = optionOf(value)
        assertThat(foldable.foldMap(monoid, { i -> "[$i]" })).isEqualTo(expected)
        assertThat(foldable.foldLeftMap(monoid, { i -> "[$i]" })).isEqualTo(expected)
    }

    @Test
    fun foldRightMapBehaveCorrectly() {
        val zero = "zero"
        val value = 42
        val expected = "[42]-zero"
        val combiner = { a: String, b: String -> "$a-$b" }
        val monoid = monoidOf(zero, combiner)

        val foldable = optionOf(value)
        assertThat(foldable.foldRightMap(monoid, { i -> "[$i]" })).isEqualTo(expected)
    }

    @Test
    fun functorIdentityLaw() {
        val value = 42
        val functorSome = optionOf(value)
        val functorNone = optionOf<String>(null)

        assertThat(functorSome.map(identity())).isEqualTo(functorSome)
        assertThat(functorNone.map(identity())).isEqualTo(functorNone)

        val functorSuccess = success(value)
        val functorFailure = failure<String>(NoSuchElementException("Failure Functor"))

        assertThat(functorSuccess.map(identity())).isEqualTo(functorSuccess)
        assertThat(functorFailure.map(identity())).isEqualTo(functorFailure)

    }

    @Test
    @Ignore
    fun functorAssociativeLaw() {
        val value = 42
        val functorSome = optionOf(value)
        val functorNone = optionOf<String>(null)


        val functorSuccess = success(value)
        val functorFailure = failure<String>(NoSuchElementException("Failure Functor"))


    }

}