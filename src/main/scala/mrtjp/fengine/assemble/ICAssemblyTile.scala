package mrtjp.fengine.assemble

trait ICAssemblyTile
{
    /**
      * Assembly pass 1:
      *
      * Allocate output register IDs and Gate IDs.
      *
      * @param assembler The assembler performing the flat mapping
      */
    def allocate(assembler:ICAssembler):Unit = {}

    /**
      * Assembly pass 2:
      *   - Use path finder to locate input register IDs.
      *   - Declare necessary remaps
      *
      * @param pathFinder Path finder for locating input registers from the map
      */
    def locate(assembler:ICAssembler, pathFinder:ICAssemblerPathFinder):Unit = {}

    /**
      * Assembly pass 3:
      *   - Check all register IDs for remap and adjust to new value if necessary.
      *
      * @param assembler The assembler performing the flat mapping
      */
    def remap(assembler:ICAssembler):Unit = {}

    /**
      * Assembly pass 4:
      *   - Add registers and gates to the assembler
      *
      * @param assembler The assembler performing the flat mapping
      */
    def collect(assembler:ICAssembler):Unit = {}
}