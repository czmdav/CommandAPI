package dev.jorel.commandapi.test.arguments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.ListArgument;
import dev.jorel.commandapi.arguments.ListArgumentBuilder;
import dev.jorel.commandapi.test.Mut;
import dev.jorel.commandapi.test.TestBase;

/**
 * Tests for the {@link ListArgument}
 */
@SuppressWarnings("unchecked")
class ArgumentListTests extends TestBase {

	/*********
	 * Setup *
	 *********/

	@BeforeEach
	public void setUp() {
		super.setUp();
	}

	@AfterEach
	public void tearDown() {
		super.tearDown();
	}

	/*********
	 * Tests *
	 *********/

	@Test
	void executionTestWithListArgument() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.withList(() -> List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildGreedy())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list cat, wolf, axolotl
		server.dispatchCommand(player, "list cat, wolf, axolotl");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		
		// /list cat, wolf, axolotl, wolf
		// No duplicates allowed
		assertCommandFailsWith(player, "list cat, wolf, axolotl, wolf", "Duplicate arguments are not allowed at position 20: ... axolotl, <--[HERE]");
		
		// /list axolotl, wolf, chicken, cat
		// Can't have unspecified "chicken" in the list
		assertCommandFailsWith(player, "list axolotl, wolf, chicken, cat", "Item is not allowed in list at position 15: ...tl, wolf, <--[HERE]");
		
		assertNoMoreResults(results);
	}
	
	@Test
	void executionTestWithListArgumentWithDuplicates() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.allowDuplicates(true)
				.withList(() -> List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildGreedy())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list cat, wolf, axolotl
		server.dispatchCommand(player, "list cat, wolf, axolotl");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		
		// /list cat, wolf, axolotl, cat, wolf
		server.dispatchCommand(player, "list cat, wolf, axolotl, cat, wolf");
		assertEquals(List.of("cat", "wolf", "axolotl", "cat", "wolf"), results.get());
		
		// /list cat, wolf, axolotl, chicken, cat
		// Can't have unspecified "chicken" in the list
		assertCommandFailsWith(player, "list cat, wolf, axolotl, chicken, cat", "Item is not allowed in list at position 20: ... axolotl, <--[HERE]");
		
		assertNoMoreResults(results);
	}
	
	@Test
	void executionTestWithListArgumentWithConstantList() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.withList(List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildGreedy())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list cat, wolf, axolotl
		server.dispatchCommand(player, "list cat, wolf, axolotl");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		
		// /list cat, wolf, axolotl, wolf
		// No duplicates allowed
		assertCommandFailsWith(player, "list cat, wolf, axolotl, wolf", "Duplicate arguments are not allowed at position 20: ... axolotl, <--[HERE]");
		
		// /list axolotl, wolf, chicken, cat
		// Can't have unspecified "chicken" in the list
		assertCommandFailsWith(player, "list axolotl, wolf, chicken, cat", "Item is not allowed in list at position 15: ...tl, wolf, <--[HERE]");
		
		assertNoMoreResults(results);
	}
	
	@Test
	void executionTestWithListArgumentWithConstantListArray() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.withList("cat", "wolf", "axolotl")
				.withStringMapper()
				.buildGreedy())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list cat, wolf, axolotl
		server.dispatchCommand(player, "list cat, wolf, axolotl");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		
		// /list cat, wolf, axolotl, wolf
		// No duplicates allowed
		assertCommandFailsWith(player, "list cat, wolf, axolotl, wolf", "Duplicate arguments are not allowed at position 20: ... axolotl, <--[HERE]");
		
		// /list axolotl, wolf, chicken, cat
		// Can't have unspecified "chicken" in the list
		assertCommandFailsWith(player, "list axolotl, wolf, chicken, cat", "Item is not allowed in list at position 15: ...tl, wolf, <--[HERE]");
		
		assertNoMoreResults(results);
	}
	
	@Test
	void executionTestWithListArgumentWithFunctionList() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.withList(player -> List.of("cat", "wolf", "axolotl", player.getName()))
				.withStringMapper()
				.buildGreedy())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list cat, wolf, axolotl
		server.dispatchCommand(player, "list cat, wolf, axolotl");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		
		// /list axolotl, wolf, APlayer
		server.dispatchCommand(player, "list axolotl, wolf, APlayer");
		assertEquals(List.of("axolotl", "wolf", "APlayer"), results.get());

		// /list cat, wolf, axolotl, wolf
		// No duplicates allowed
		assertCommandFailsWith(player, "list cat, wolf, axolotl, wolf", "Duplicate arguments are not allowed at position 20: ... axolotl, <--[HERE]");
		
		// /list axolotl, wolf, chicken, cat
		// Can't have unspecified "chicken" in the list
		assertCommandFailsWith(player, "list axolotl, wolf, chicken, cat", "Item is not allowed in list at position 15: ...tl, wolf, <--[HERE]");
		
		assertNoMoreResults(results);
	}

	/***************************************
	 * Test ListArgument with .buildText() *
	 ***************************************/

	@Test
	void executionTestWithListTextArgument() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.withList(() -> List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildText())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list "cat, wolf, axolotl"
		server.dispatchCommand(player, "list \"cat, wolf, axolotl\"");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		
		// /list "cat, wolf, axolotl, wolf"
		// No duplicates allowed
		assertCommandFailsWith(player, "list \"cat, wolf, axolotl, wolf\"", "Duplicate arguments are not allowed at position 20: ... axolotl, <--[HERE]");
		
		// /list "axolotl, wolf, chicken, cat"
		// Can't have unspecified "chicken" in the list
		assertCommandFailsWith(player, "list \"axolotl, wolf, chicken, cat\"", "Item is not allowed in list at position 15: ...tl, wolf, <--[HERE]");
		
		assertNoMoreResults(results);
	}
	
	@Test
	void executionTestWithListTextArgumentWithDuplicates() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.allowDuplicates(true)
				.withList(() -> List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildText())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list "cat, wolf, axolotl"
		server.dispatchCommand(player, "list \"cat, wolf, axolotl\"");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		
		// /list "cat, wolf, axolotl, cat, wolf"
		server.dispatchCommand(player, "list \"cat, wolf, axolotl, cat, wolf\"");
		assertEquals(List.of("cat", "wolf", "axolotl", "cat", "wolf"), results.get());
		
		// /list "cat, wolf, axolotl, chicken, cat"
		// Can't have unspecified "chicken" in the list
		assertCommandFailsWith(player, "list \"cat, wolf, axolotl, chicken, cat\"", "Item is not allowed in list at position 20: ... axolotl, <--[HERE]");
		
		assertNoMoreResults(results);
	}
	
	@Test
	void executionTestWithListTextArgumentWithConstantList() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.withList(List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildText())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list "cat, wolf, axolotl"
		server.dispatchCommand(player, "list \"cat, wolf, axolotl\"");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		
		// /list "cat, wolf, axolotl, wolf"
		// No duplicates allowed
		assertCommandFailsWith(player, "list \"cat, wolf, axolotl, wolf\"", "Duplicate arguments are not allowed at position 20: ... axolotl, <--[HERE]");
		
		// /list "axolotl, wolf, chicken, cat"
		// Can't have unspecified "chicken" in the list
		assertCommandFailsWith(player, "list \"axolotl, wolf, chicken, cat\"", "Item is not allowed in list at position 15: ...tl, wolf, <--[HERE]");
		
		assertNoMoreResults(results);
	}
	
	@Test
	void executionTestWithListTextArgumentWithFunctionList() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.withList(player -> List.of("cat", "wolf", "axolotl", player.getName()))
				.withStringMapper()
				.buildText())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list "cat, wolf, axolotl"
		server.dispatchCommand(player, "list \"cat, wolf, axolotl\"");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		
		// /list "axolotl, wolf, APlayer"
		server.dispatchCommand(player, "list \"axolotl, wolf, APlayer\"");
		assertEquals(List.of("axolotl", "wolf", "APlayer"), results.get());

		// /list "cat, wolf, axolotl, wolf"
		// No duplicates allowed
		assertCommandFailsWith(player, "list \"cat, wolf, axolotl, wolf\"", "Duplicate arguments are not allowed at position 20: ... axolotl, <--[HERE]");
		
		// /list "axolotl, wolf, chicken, cat"
		// Can't have unspecified "chicken" in the list
		assertCommandFailsWith(player, "list \"axolotl, wolf, chicken, cat\"", "Item is not allowed in list at position 15: ...tl, wolf, <--[HERE]");
		
		assertNoMoreResults(results);
	}

	@Test
	void executionTestWithListTextArgumentAfterListTextArgument() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.withList(player -> List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildText())
			.withArguments(new ListArgumentBuilder<>("morevalues", ", ")
				.withList(player -> List.of("pumpkin", "melon", "cake"))
				.withStringMapper()
				.buildText())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
				results.set((List<String>) args.get(1));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list "cat, wolf, axolotl" "pumpkin melon"
		server.dispatchCommand(player, "list \"cat, wolf, axolotl\" \"pumpkin, melon\"");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		assertEquals(List.of("pumpkin", "melon"), results.get());
		
		assertNoMoreResults(results);
	}

	/****************************
	 * Other ListArgument tests *
	 ****************************/

	@Test
	void executionTestWithListArgumentWithNonStringMapper() {
		Mut<List<Material>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<Material>("values", ", ")
				.withList(() -> List.of(Material.DIRT, Material.GRASS_BLOCK, Material.DIAMOND))
				.withMapper(material -> material.getKey().getKey())
				.buildGreedy())
			.executesPlayer((player, args) -> {
				results.set((List<Material>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list dirt, grass_block
		server.dispatchCommand(player, "list dirt, grass_block");
		assertEquals(List.of(Material.DIRT, Material.GRASS_BLOCK), results.get());
		
		// /list dirt, dirt
		// No duplicates allowed
		assertCommandFailsWith(player, "list dirt, dirt", "Duplicate arguments are not allowed at position 6: dirt, <--[HERE]");
		
		// /list iron_ingot
		// Can't have unspecified "iron_ingot" in the list
		assertCommandFailsWith(player, "list iron_ingot", "Item is not allowed in list at position 0: <--[HERE]");
		
		assertNoMoreResults(results);
	}
	
	@Test
	void executionTestWithListArgumentWithDefaultDelimiter() {
		Mut<List<String>> results = Mut.of();

		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values")
				.withList(() -> List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildGreedy())
			.executesPlayer((player, args) -> {
				results.set((List<String>) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer("APlayer");
		
		// /list cat wolf axolotl
		server.dispatchCommand(player, "list cat wolf axolotl");
		assertEquals(List.of("cat", "wolf", "axolotl"), results.get());
		
		// /list cat wolf axolotl wolf
		// No duplicates allowed
		assertCommandFailsWith(player, "list cat wolf axolotl wolf", "Duplicate arguments are not allowed at position 17: ...f axolotl <--[HERE]");
		
		// /list axolotl wolf chicken, cat
		// Can't have unspecified "chicken" in the list
		assertCommandFailsWith(player, "list axolotl wolf chicken cat", "Item is not allowed in list at position 13: ...lotl wolf <--[HERE]");
		
		assertNoMoreResults(results);
	}
	
	/********************
	 * Suggestion tests *
	 ********************/

	@Test
	void suggestionTestWithListArgument() {
		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values")
				.withList(() -> List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildGreedy())
			.executesPlayer((player, args) -> {
			})
		.register();

		PlayerMock player = server.addPlayer();

		// /list
		// All values should be suggested
		assertEquals(List.of("axolotl", "cat", "wolf"), server.getSuggestions(player, "list "));
		
		// /list cat 
		// All values should be suggested except for "cat"
		assertEquals(List.of("axolotl", "wolf"), server.getSuggestions(player, "list cat "));
	}

	@Test
	void suggestionTestWithListArgumentWithDuplicates() {
		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values")
				.allowDuplicates(true)
				.withList(() -> List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildGreedy())
			.executesPlayer((player, args) -> {
			})
		.register();

		PlayerMock player = server.addPlayer();

		// /list
		// All values should be suggested
		assertEquals(List.of("axolotl", "cat", "wolf"), server.getSuggestions(player, "list "));
		
		// /list cat 
		// All values should also be suggested
		assertEquals(List.of("axolotl", "cat", "wolf"), server.getSuggestions(player, "list cat "));
	}
	
	@Test
	void suggestionTestWithListArgumentDelimiter() {
		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values", ", ")
				.withList(() -> List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildGreedy())
			.executesPlayer((player, args) -> {
			})
		.register();

		PlayerMock player = server.addPlayer();

		// /list
		// The delimiter should be suggested
		assertEquals(List.of("cat, "), server.getSuggestions(player, "list cat"));
	}
	
	@Test
	void suggestionTestWithListArgumentText() {
		new CommandAPICommand("list")
			.withArguments(new ListArgumentBuilder<>("values")
				.withList(() -> List.of("cat", "wolf", "axolotl"))
				.withStringMapper()
				.buildText())
			.executesPlayer((player, args) -> {
			})
		.register();

		PlayerMock player = server.addPlayer();

		// /list "cat
		// All values should be suggested except for "cat"
		assertEquals(List.of("axolotl", "wolf"), server.getSuggestions(player, "list \"cat "));
	}
}
