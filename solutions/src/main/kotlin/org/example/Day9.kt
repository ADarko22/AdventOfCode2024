package org.example

fun main() {
    Day9().part1()
    Day9().part2()
}

class Day9 : Day {

    private val input = "/day9".readResourceLines()[0]

    override fun part1() {
        val solution = computeFileSystemChecksum(deFragmentBySplittingFiles())
        println("Day 9 part 1: $solution")
    }

    override fun part2() {
        val solution = computeFileSystemChecksum(deFragmentByMovingWholeFile())
        println("Day 9 part 2: $solution")
    }

    private data class DiskBlock(val files: MutableList<File> = mutableListOf(), var freeSpace: Long = 0L)

    private data class File(val id: Int, var size: Long)


    private fun getDiskBlocks(): List<DiskBlock> =
        IntRange(0, input.length / 2)
            .map {
                val diskBlock = DiskBlock()

                if (2 * it < input.length) {
                    diskBlock.files.add(File(it, input[2 * it].digitToInt().toLong()))
                }

                if (2 * it + 1 < input.length) {
                    diskBlock.freeSpace = input[2 * it + 1].digitToInt().toLong()
                }

                diskBlock
            }

    private fun computeFileSystemChecksum(deFragmentedDiskBlocks: List<DiskBlock>): Long {
        var checksum = 0L
        var posIdx = 0L

        for (diskBlock in deFragmentedDiskBlocks) {
            for (file in diskBlock.files) {
                // sum of arithmetic progression from posIdx to posIdx + diskBlock.size - 1
                val progressionSumOfPosIdx = (posIdx + posIdx + file.size - 1) * file.size / 2
                checksum += (progressionSumOfPosIdx * file.id)
                posIdx += file.size
            }
            posIdx += diskBlock.freeSpace
        }

        return checksum
    }

    private fun deFragmentBySplittingFiles(): List<DiskBlock> {
        val diskBlocks = getDiskBlocks()

        var diskBlockWithFreeSpaceIdx = 0
        var diskBlockWithFileToMoveIdx = diskBlocks.size - 1

        while (diskBlockWithFreeSpaceIdx < diskBlockWithFileToMoveIdx) {
            val fileToMoveId = diskBlocks[diskBlockWithFileToMoveIdx].files[0].id
            val remainingFileSize = diskBlocks[diskBlockWithFileToMoveIdx].files[0].size

            if (diskBlocks[diskBlockWithFreeSpaceIdx].freeSpace > remainingFileSize) {
                // move file to disk block with free space
                diskBlocks[diskBlockWithFreeSpaceIdx].files.add(File(fileToMoveId, remainingFileSize))

                // file fully moved
                diskBlocks[diskBlockWithFileToMoveIdx].files.removeFirst()
                diskBlockWithFileToMoveIdx--

                diskBlocks[diskBlockWithFreeSpaceIdx].freeSpace -= remainingFileSize

            } else if (diskBlocks[diskBlockWithFreeSpaceIdx].freeSpace == remainingFileSize) {
                // move file to disk block with free space
                diskBlocks[diskBlockWithFreeSpaceIdx].files.add(File(fileToMoveId, remainingFileSize))

                // file fully moved
                diskBlocks[diskBlockWithFileToMoveIdx].files.removeFirst()
                diskBlockWithFileToMoveIdx--

                // free space fully used
                diskBlocks[diskBlockWithFreeSpaceIdx].freeSpace = 0
                diskBlockWithFreeSpaceIdx++

            } else {
                // move file to disk block with free space
                diskBlocks[diskBlockWithFreeSpaceIdx].files
                    .add(File(fileToMoveId, diskBlocks[diskBlockWithFreeSpaceIdx].freeSpace))

                diskBlocks[diskBlockWithFileToMoveIdx].files[0].size -= diskBlocks[diskBlockWithFreeSpaceIdx].freeSpace
                diskBlocks[diskBlockWithFreeSpaceIdx].freeSpace = 0

                diskBlockWithFreeSpaceIdx++
            }
        }

        return diskBlocks
    }

    private fun deFragmentByMovingWholeFile(): List<DiskBlock> {
        val diskBlocks = getDiskBlocks()

        var diskBlockWithFileToMoveIdx = diskBlocks.size - 1

        while (diskBlockWithFileToMoveIdx > 1) {
            val fileToMove = diskBlocks[diskBlockWithFileToMoveIdx].files[0]

            for (diskBlockWithFreeSpaceIdx in 0 until diskBlockWithFileToMoveIdx) {
                if (diskBlocks[diskBlockWithFreeSpaceIdx].freeSpace >= fileToMove.size) {
                    diskBlocks[diskBlockWithFreeSpaceIdx].files.add(fileToMove)
                    diskBlocks[diskBlockWithFreeSpaceIdx].freeSpace -= fileToMove.size

                    // when removing a file add the free space to the previous block
                    val removedFile = diskBlocks[diskBlockWithFileToMoveIdx].files.removeFirst()
                    diskBlocks[diskBlockWithFileToMoveIdx - 1].freeSpace += removedFile.size
                    break
                }
            }

            diskBlockWithFileToMoveIdx--
        }

        return diskBlocks
    }

}