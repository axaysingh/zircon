package org.codetome.zircon.internal.behavior.impl

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.junit.Before
import org.junit.Test

class DefaultScrollableTest {

    lateinit var target: DefaultScrollable

    @Before
    fun setUp() {
        target = DefaultScrollable(
                visibleSpaceSize = VISIBLE_SPACE_SIZE,
                virtualSpaceSize = VIRTUAL_SPACE_SIZE)
    }

    @Test
    fun shouldProperlyReportVirtualSpaceSize() {
        assertThat(target.getVirtualSpaceSize())
                .isEqualTo(VIRTUAL_SPACE_SIZE)
    }

    @Test
    fun shouldProperlyScrollOneRightWhenCanScroll() {
        target.scrollOneRight()

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(1, 0))
    }

    @Test
    fun shouldProperlyScrollOneLeftWhenCanScroll() {
        target.scrollOneRight()
        target.scrollOneRight()
        target.scrollOneLeft()

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(1, 0))
    }

    @Test
    fun shouldProperlyScrollOneDownWhenCanScroll() {
        target.scrollOneDown()

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(0, 1))
    }

    @Test
    fun shouldProperlyScrollOneUpWhenCanScroll() {
        target.scrollOneDown()
        target.scrollOneDown()
        target.scrollOneUp()

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(0, 1))
    }

    @Test
    fun shouldProperlyScrollRightWhenCanScroll() {
        target.scrollRightBy(5)

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(5, 0))
    }

    @Test
    fun shouldProperlyScrollLeftWhenCanScroll() {
        target.scrollRightBy(5)
        target.scrollLeftBy(3)

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(2, 0))
    }

    @Test
    fun shouldProperlyScrollDownWhenCanScroll() {
        target.scrollDownBy(5)

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(0, 5))
    }

    @Test
    fun shouldProperlyScrollUpWhenCanScroll() {
        target.scrollDownBy(5)
        target.scrollUpBy(3)

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(0, 2))
    }

    @Test
    fun shouldProperlyScrollRightToMaxWhenScrollingTooMuch() {
        target.scrollRightBy(Int.MAX_VALUE)

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(5, 0))
    }

    @Test
    fun shouldProperlyScrollLeftToMaxWhenScrollingTooMuch() {
        target.scrollRightBy(5)
        target.scrollLeftBy(Int.MAX_VALUE)

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(0, 0))
    }

    @Test
    fun shouldProperlyScrollUpToMaxWhenScrollingTooMuch() {
        target.scrollDownBy(5)
        target.scrollUpBy(Int.MAX_VALUE)

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(0, 0))
    }

    @Test
    fun shouldProperlyScrollDownToMaxWhenScrollingTooMuch() {
        target.scrollDownBy(Int.MAX_VALUE)

        assertThat(target.getVisibleOffset())
                .isEqualTo(Position.of(0, 5))
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionWhenTryingToScrollRightByNegativeAmount() {
        target.scrollRightBy(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionWhenTryingToScrollLeftByNegativeAmount() {
        target.scrollLeftBy(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionWhenTryingToScrollUpByNegativeAmount() {
        target.scrollUpBy(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionWhenTryingToScrollDownByNegativeAmount() {
        target.scrollDownBy(-1)
    }

    companion object {
        val VIRTUAL_SPACE_SIZE = Size.of(10, 10)
        val VISIBLE_SPACE_SIZE = Size.of(5, 5)
    }

}