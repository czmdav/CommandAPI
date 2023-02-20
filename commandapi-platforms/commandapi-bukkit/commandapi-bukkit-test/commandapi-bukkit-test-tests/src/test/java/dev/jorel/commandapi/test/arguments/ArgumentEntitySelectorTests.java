package dev.jorel.commandapi.test.arguments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.test.Mut;
import dev.jorel.commandapi.test.TestBase;

/**
 * Tests for the {@link EntitySelectorArgument}
 */
class ArgumentEntitySelectorTests extends TestBase {

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
	void executionTestWithEntitySelectorArgumentOnePlayer() {
		Mut<Player> results = Mut.of();
		
		new CommandAPICommand("test")
			.withArguments(new EntitySelectorArgument.OnePlayer("value"))
			.executesPlayer((player, args) -> {
				results.set((Player) args.get(0));
			})
			.register();
		
		PlayerMock player = server.addPlayer("APlayer");
		
		// /test APlayer
		server.dispatchCommand(player, "test APlayer");
		assertEquals("APlayer", results.get().getName());
		
		// /test @p
		server.dispatchCommand(player, "test @p");
		assertEquals("APlayer", results.get().getName());
		
		// /test @a[limit=1]
		server.dispatchCommand(player, "test @a[limit=1]");
		assertEquals("APlayer", results.get().getName());
		
		// /test @e[limit=1]
		// Fails because @e can accept any entity, but we only want one player
		assertCommandFailsWith(player, "test @e[limit=1]", "Only players may be affected by this command, but the provided selector includes entities at position 0: <--[HERE]");
		
		// /test @e[limit=1,type=player]
		// Should NOT fail because we've specified that the entity type is a player
		// Dev note: This command fails because no entities are found, most likely due to entity lookups not working in the test environment.
		assertNotCommandFailsWith(player, "test @e[limit=1,type=player]", "Only players may be affected by this command, but the provided selector includes entities at position 0: <--[HERE]");
		
		// /test @a
		// Fails because @a can allow more than one player, but we've specified only one player
		assertCommandFailsWith(player, "test @a", "Only one player is allowed, but the provided selector allows more than one at position 0: <--[HERE]");

		assertNoMoreResults(results);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void executionTestWithEntitySelectorArgumentManyPlayers() {
		Mut<Collection<Player>> results = Mut.of();
		
		new CommandAPICommand("test")
			.withArguments(new EntitySelectorArgument.ManyPlayers("value"))
			.executesPlayer((player, args) -> {
				results.set((Collection<Player>) args.get(0));
			})
			.register();
		
		PlayerMock player = server.addPlayer("APlayer");
		server.addPlayer("APlayer1");
		server.addPlayer("APlayer2");
		server.addPlayer("APlayer3");
		server.addPlayer("APlayer4");
		
		// /test @a
		server.dispatchCommand(player, "test @a");
		assertEquals(List.of("APlayer", "APlayer1", "APlayer2", "APlayer3", "APlayer4"), results.get().stream().map(Player::getName).toList());
		
		assertNoMoreResults(results);
	}

	@Test
	void executionTestWithEntitySelectorArgumentOneEntity() {
		Mut<Entity> results = Mut.of();
		
		new CommandAPICommand("test")
			.withArguments(new EntitySelectorArgument.OneEntity("value"))
			.executesPlayer((player, args) -> {
				results.set((Entity) args.get(0));
			})
			.register();
		
		PlayerMock player = server.addPlayer("APlayer");
		
		// /test APlayer
		server.dispatchCommand(player, "test APlayer");
		assertEquals("APlayer", results.get().getName());
		
		// /test @p
		server.dispatchCommand(player, "test @p");
		assertEquals("APlayer", results.get().getName());
		
		// /test @e
		// Fails because @e can allow more than one entity, but we've specified only one entity
		assertCommandFailsWith(player, "test @e", "Only one entity is allowed, but the provided selector allows more than one at position 0: <--[HERE]");
		
		// /test @a[limit=1]
		// Should NOT fail with "Only one entity is allowed" because we've added a single entity limiter to it.
		// Dev note: This fails because no entities are found, most likely due to entity lookups not working in the test environment.
		assertNotCommandFailsWith(player, "test @e[limit=1]", "Only one entity is allowed, but the provided selector allows more than one at position 0: <--[HERE]");
		
		// /test @a
		// Dev note: This command fails because no entities are found, most likely due to entity lookups not working in the test environment.
		assertCommandFailsWith(player, "test @a", "Only one entity is allowed, but the provided selector allows more than one at position 0: <--[HERE]");

		assertNoMoreResults(results);
	}

	@SuppressWarnings("unchecked")
	@Test
	void executionTestWithEntitySelectorArgumentManyEntities() {
		Mut<Collection<Entity>> results = Mut.of();
		
		new CommandAPICommand("test")
			.withArguments(new EntitySelectorArgument.ManyEntities("value"))
			.executesPlayer((player, args) -> {
				results.set((Collection<Entity>) args.get(0));
			})
			.register();
		
		PlayerMock player = server.addPlayer("APlayer");
		server.addPlayer("APlayer1");
		server.addPlayer("APlayer2");
		server.addPlayer("APlayer3");
		server.addPlayer("APlayer4");
		
		// /test @a
		server.dispatchCommand(player, "test @a");
		assertEquals(List.of("APlayer", "APlayer1", "APlayer2", "APlayer3", "APlayer4"), results.get().stream().map(Entity::getName).toList());
		
		// /test APlayer
		server.dispatchCommand(player, "test APlayer");
		assertEquals(1, results.get().size());
		
		// /test @p
		server.dispatchCommand(player, "test @p");
		assertEquals(1, results.get().size());

		assertNoMoreResults(results);
	}

}
