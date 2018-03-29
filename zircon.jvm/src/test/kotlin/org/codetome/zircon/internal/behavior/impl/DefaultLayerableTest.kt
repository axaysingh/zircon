package org.codetome.zircon.internal.behavior.impl

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.LayerBuilder
import org.codetome.zircon.api.builder.TextCharacterBuilder
import org.codetome.zircon.api.builder.TextCharacterBuilder.Companion.DEFAULT_CHARACTER
import org.codetome.zircon.api.builder.TextImageBuilder
import org.codetome.zircon.api.font.Font
import org.codetome.zircon.api.resource.CP437TilesetResource
import org.codetome.zircon.internal.font.FontLoaderRegistry
import org.codetome.zircon.internal.font.impl.TestFontLoader
import org.codetome.zircon.internal.font.impl.VirtualFontLoader
import org.junit.Before
import org.junit.Test

class DefaultLayerableTest {

    lateinit var target: DefaultLayerable
    lateinit var font: Font

    @Before
    fun setUp() {
        FontLoaderRegistry.setFontLoader(TestFontLoader())
        font = FONT.toFont()
        target = DefaultLayerable(
                size = SIZE,
                supportedFontSize = font.getSize())
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionWhenLayerUsesUnsupportedFontSize() {
        val layer = LayerBuilder.newBuilder()
                .size(Size.ONE)
                .filler(DEFAULT_CHARACTER)
                .font(CP437TilesetResource.BISASAM_20X20.toFont())
                .offset(Position.TOP_LEFT_CORNER)
                .build()


        target.pushLayer(layer)
    }

    @Test
    fun shouldContainLayerWhenLayerIsAdded() {
        val layer = LayerBuilder.newBuilder()
                .size(Size.ONE)
                .filler(DEFAULT_CHARACTER)
                .offset(Position.TOP_LEFT_CORNER)
                .build()

        target.pushLayer(layer)

        assertThat(target.fetchOverlayZIntersection(Position.TOP_LEFT_CORNER))
                .isNotEmpty

    }

    @Test
    fun shouldNotContainLayerWhenLayerIsAddedThenRemoved() {
        val layer = LayerBuilder.newBuilder()
                .size(Size.ONE)
                .filler(DEFAULT_CHARACTER)
                .offset(Position.TOP_LEFT_CORNER)
                .build()

        target.pushLayer(layer)
        target.removeLayer(layer)

        assertThat(target.fetchOverlayZIntersection(Position.TOP_LEFT_CORNER))
                .isEmpty()

    }

    @Test
    fun shouldNotContainLayerWhenLayerIsAddedThenPopped() {
        val layer = LayerBuilder.newBuilder()
                .size(Size.ONE)
                .filler(DEFAULT_CHARACTER)
                .offset(Position.TOP_LEFT_CORNER)
                .build()

        target.pushLayer(layer)
        val result = target.popLayer()

        assertThat(target.fetchOverlayZIntersection(Position.TOP_LEFT_CORNER))
                .isEmpty()
        assertThat(result.get()).isSameAs(layer)

    }

    @Test
    fun shouldContainBottomLayerOnlyWhenTwoLayersAreAddedAndTopDoesNotIntersectCoordinate() {
        val expectedChar = TextCharacterBuilder.newBuilder()
                .character('1')
                .build()

        val offset1x1layer = LayerBuilder.newBuilder()
                .size(Size.ONE)
                .filler(expectedChar)
                .offset(Position.OFFSET_1x1)
                .build()
        val offset2x2layer = LayerBuilder.newBuilder()
                .size(Size.ONE)
                .filler(TextCharacterBuilder.newBuilder()
                        .character('2')
                        .build())
                .offset(Position.of(2, 2))
                .build()


        target.pushLayer(offset1x1layer)
        target.pushLayer(offset2x2layer)

        val result = target.fetchOverlayZIntersection(Position.OFFSET_1x1)


        assertThat(result.map { it.second }).containsExactly(expectedChar)
    }

    @Test
    fun shouldProperlyMarkDrainedLayerPositionsDirtyWhenLayersAreDrained() {
        val dirty0 = Position.of(1, 2)
        val dirty1 = Position.of(3, 4)

        target.pushLayer(LayerBuilder.newBuilder()
                .offset(dirty0)
                .textImage(TextImageBuilder.newBuilder()
                        .size(Size.ONE)
                        .character(Position.DEFAULT_POSITION, TextCharacterBuilder.DEFAULT_CHARACTER.withCharacter('x'))
                        .build())
                .build())

        target.pushLayer(LayerBuilder.newBuilder()
                .offset(dirty1)
                .textImage(TextImageBuilder.newBuilder()
                        .size(Size.ONE)
                        .character(Position.DEFAULT_POSITION, TextCharacterBuilder.DEFAULT_CHARACTER.withCharacter('x'))
                        .build())
                .build())

        target.drainLayers()
        assertThat(target.drainDirtyPositions())
                .containsExactlyInAnyOrder(dirty0, dirty1)
    }

    @Test
    fun shouldProperlyNotMarkDrainedLayerPositionsDirtyWhenTheyAreEmpty() {
        val dirty0 = Position.of(1, 2)
        val dirty1 = Position.of(3, 4)

        target.pushLayer(LayerBuilder.newBuilder()
                .offset(dirty0)
                .build())

        target.pushLayer(LayerBuilder.newBuilder()
                .offset(dirty1)
                .build())

        target.drainLayers()
        assertThat(target.drainDirtyPositions()).isEmpty()
    }

    @Test
    fun shouldContainAllLayersWhenTwoLayersAreAddedAndTheyIntersect() {
        val expectedChar = TextCharacterBuilder.newBuilder()
                .character('1')
                .build()

        val offset1x1layer = LayerBuilder.newBuilder()
                .size(Size.ONE)
                .filler(expectedChar)
                .offset(Position.OFFSET_1x1)
                .build()

        val offset2x2layer = LayerBuilder.newBuilder()
                .size(Size.ONE)
                .filler(expectedChar)
                .offset(Position.OFFSET_1x1)
                .build()

        target.pushLayer(offset1x1layer)
        target.pushLayer(offset2x2layer)

        val result = target.fetchOverlayZIntersection(Position.OFFSET_1x1)


        assertThat(result.map { it.second }).containsExactly(expectedChar, expectedChar)
    }

    companion object {
        val SIZE = Size.of(10, 10)
        val FONT = CP437TilesetResource.WANDERLUST_16X16
    }
}
