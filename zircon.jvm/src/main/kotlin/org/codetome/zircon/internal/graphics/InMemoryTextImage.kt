package org.codetome.zircon.internal.graphics

import org.codetome.zircon.api.Cell
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.TextCharacter
import org.codetome.zircon.api.behavior.DrawSurface
import org.codetome.zircon.api.builder.StyleSetBuilder
import org.codetome.zircon.api.builder.TextCharacterBuilder
import org.codetome.zircon.api.graphics.StyleSet
import org.codetome.zircon.api.graphics.TextImage
import org.codetome.zircon.api.graphics.TextImageBase
import org.codetome.zircon.api.sam.TextCharacterTransformer
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class InMemoryTextImage(size: Size,
                        styleSet: StyleSet = StyleSetBuilder.DEFAULT_STYLE,
                        chars: Map<Position, TextCharacter> = mapOf(),
                        private val filler: TextCharacter = TextCharacterBuilder.EMPTY)
    : TextImageBase(size = size, styleSet = styleSet) {

    private val backend = ConcurrentHashMap<Position, TextCharacter>(chars)

    override fun toString(): String {
        return (0 until getBoundableSize().yLength).joinToString("") { y ->
            (0 until getBoundableSize().xLength).map { x ->
                backend.getOrDefault(Position.of(x, y), filler).getCharacter()
            }.joinToString("").plus("\n")
        }
    }

    override fun fetchFilledPositions() = backend.keys.sorted()

    override fun getCharacterAt(position: Position): Optional<TextCharacter> {
        return if (getBoundableSize().containsPosition(position)) {
            Optional.ofNullable(backend.getOrDefault(position, filler))
        } else {
            Optional.empty()
        }
    }

    override fun setCharacterAt(position: Position, character: TextCharacter): Boolean {
        return if (getBoundableSize().containsPosition(position)) {
            backend[position] = character
            true
        } else {
            false
        }
    }

    override fun combineWith(textImage: TextImage, offset: Position): TextImage {
        val columns = Math.max(getBoundableSize().xLength, offset.x + textImage.getBoundableSize().xLength)
        val rows = Math.max(getBoundableSize().yLength, offset.y + textImage.getBoundableSize().yLength)

        val surface = resize(Size.of(columns, rows), filler)
        surface.draw(textImage, offset)
        return surface
    }

    override fun drawOnto(surface: DrawSurface, offset: Position) {
        getBoundableSize().fetchPositions().forEach {
            surface.setCharacterAt(it + offset, backend.getOrDefault(it, filler))
        }
    }

    override fun fetchCellsBy(offset: Position, size: Size): Iterable<Cell> {
        return size.fetchPositions()
                .map { it + offset }
                .map { Cell(it, getCharacterAt(it).get()) }
    }

    override fun resize(newSize: Size, filler: TextCharacter): TextImage {
        val result = InMemoryTextImage(
                size = newSize,
                styleSet = toStyleSet(),
                filler = this.filler)
        backend.filter { (pos) -> newSize.containsPosition(pos) }
                .forEach { pos, tc ->
                    result.setCharacterAt(pos, tc)
                }

        newSize.fetchPositions().subtract(getBoundableSize().fetchPositions()).forEach {
            result.setCharacterAt(it, filler)
        }
        return result
    }

    override fun toSubImage(offset: Position, size: Size): TextImage {
        val result = InMemoryTextImage(size, toStyleSet())
        size.fetchPositions()
                .map { it + offset }
                .intersect(getBoundableSize().fetchPositions())
                .forEach {
                    result.setCharacterAt(it - offset, getCharacterAt(it).get())
                }
        return result
    }

    override fun transform(transformer: TextCharacterTransformer): TextImage {
        val result = InMemoryTextImage(getBoundableSize(), toStyleSet())
        fetchCells().forEach { (pos, char) ->
            result.setCharacterAt(pos, transformer.transform(char))
        }
        return result
    }
}
