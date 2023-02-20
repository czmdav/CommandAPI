package dev.jorel.commandapi.test.dsltests

import be.seeseemelk.mockbukkit.entity.PlayerMock
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.test.Mut
import dev.jorel.commandapi.test.TestBase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommandTreeTests : TestBase() {

	/*********
	 * Setup *
	 *********/

	@BeforeEach
	override fun setUp() {
		super.setUp()
	}

	@AfterEach
	override fun tearDown() {
		super.tearDown()
	}

	/*********
	 * Tests *
	 *********/

	@Test
	fun executionTestWithCommandTree() {
		val results: Mut<String> = Mut.of()

		commandTree("test") {
			playerExecutor { player, args ->
				results.set("Test successful!")
			}
		}

		val player: PlayerMock = server.addPlayer()

		// /test
		server.dispatchCommand(player, "test")
		assertEquals("Test successful!", results.get())

		assertNoMoreResults(results)
	}

	@Test
	fun executionTestWithCommandTreeWithBranches() {
		val results: Mut<String> = Mut.of()

		commandTree("test") {
			literalArgument("one") {
				playerExecutor { player, args ->
					results.set("/test one")
				}
			}
			literalArgument("two") {
				playerExecutor { player, args ->
					results.set("/test two")
				}
				literalArgument("three") {
					playerExecutor { player, args ->
						results.set("/test two three")
					}
				}
			}
		}

		val player: PlayerMock = server.addPlayer()

		// /test one
		server.dispatchCommand(player, "test one")
		assertEquals("/test one", results.get())

		// /test two
		server.dispatchCommand(player, "test two")
		assertEquals("/test two", results.get())

		// /test two three
		server.dispatchCommand(player, "test two three")
		assertEquals("/test two three", results.get())

		// /test
		assertCommandFailsWith(player, "test", "Unknown or incomplete command, see below for error at position 4: test<--[HERE]")

		// /test three
		assertCommandFailsWith(player, "test three", "Incorrect argument for command at position 5: test <--[HERE]")

		assertNoMoreResults(results)
	}

}