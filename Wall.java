import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Wall implements Structure {
    public List<Block> blocks;

    @Override
    public Optional<Block> findBlockByColor(String color) {
        return findByPredicate(e -> e.getColor().equals(color)).findAny();
    }

    @Override
    public List<Block> findBlocksByMaterial(String material) {
        return findByPredicate(e -> e.getMaterial().equals(material)).toList();
    }

    @Override
    public int count() {
        // only block are counted in similar way that bricks are counted in a wall without composite composed of blocks
        return blocks.parallelStream().mapToInt(e -> (e instanceof CompositeBlock ? ((CompositeBlock) e).getBlocks().size() : 1)).reduce(0, Integer::sum);
    }

    /**
     *
     * @param blockPredicate used to filter the collection of blocks
     * @return stream containing filtered blocks
     */
    private Stream<Block> findByPredicate(Predicate<Block> blockPredicate) {
        return getFlatMapped().filter(blockPredicate);
    }

    /**
     * @return block map flattened according to specific algorithm
     */
    private Stream<Block> getFlatMapped() {
        return blocks.parallelStream().flatMap(Wall::flattenCompositesAndBlocks);
    }

    /**
     * @param block single block from the list
     * @return Stream of blocks
     */
    private static Stream<Block> flattenCompositesAndBlocks(Block block) {
        // I assume that the composite may have its general color & material (e.g most of the blocks are from
        // wood so the composite is from wood)
        // but child nodes have also their colors & material so the composite may be made out of wood but some blocks out of metal
        // in other cases just returning getBlocks() is enough since the getColor() in impl may return null
        // also polymorphism may be used in toStream impl to avoid this if but since I was asked to put all logic here I leave it explicit here
        if(block instanceof CompositeBlock) {
            return Stream.concat(((CompositeBlock) block).getBlocks().stream(), Stream.of(block));
        }

        return Stream.of(block);
    }
}
