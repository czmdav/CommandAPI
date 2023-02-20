package dev.jorel.commandapi.test.arguments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.AdvancementArgument;
import dev.jorel.commandapi.test.Mut;
import dev.jorel.commandapi.test.TestBase;

/**
 * Tests for the {@link dev.jorel.commandapi.arguments.AdvancementArgument}
 */
class ArgumentAdvancementTests extends TestBase {

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
	void executionTestWithAdvancementArgument() {
		Mut<Advancement> results = Mut.of();

		new CommandAPICommand("test")
			.withArguments(new AdvancementArgument("advancement"))
			.executesPlayer((player, args) -> {
				results.set((Advancement) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer();
		
		server.addAdvancement(NamespacedKey.minecraft("adventure/adventuring_time"));

		// /test adventure/adventuring_time
		server.dispatchCommand(player, "test adventure/adventuring_time");
		assertEquals(NamespacedKey.minecraft("adventure/adventuring_time"), results.get().getKey());
		
		// /test minecraft:adventure/adventuring_time
		server.dispatchCommand(player, "test minecraft:adventure/adventuring_time");
		assertEquals(NamespacedKey.minecraft("adventure/adventuring_time"), results.get().getKey());

		// /test namespace:group/unknown_advancement
		assertCommandFailsWith(player, "test namespace:group/unknown_advancement", "Unknown advancement: namespace:group/unknown_advancement");

		assertNoMoreResults(results);
	}

	/********************
	 * Suggestion tests *
	 ********************/

	@Test
	void suggestionTestWithAxisArgument() {
		new CommandAPICommand("test")
			.withArguments(new AdvancementArgument("advancement"))
			.executesPlayer((player, args) -> {
			})
			.register();

		PlayerMock player = server.addPlayer();
		
		List<NamespacedKey> advancements = List.of(
			NamespacedKey.minecraft("adventure/root"),
			NamespacedKey.minecraft("adventure/voluntary_exile"),
			NamespacedKey.minecraft("adventure/spyglass_at_parrot"),
			NamespacedKey.minecraft("adventure/kill_a_mob"),
			NamespacedKey.minecraft("adventure/trade"),
			NamespacedKey.minecraft("adventure/honey_block_slide"),
			NamespacedKey.minecraft("adventure/ol_betsy"),
			NamespacedKey.minecraft("adventure/lightning_rod_with_villager_no_fire"),
			NamespacedKey.minecraft("adventure/fall_from_world_height"),
			NamespacedKey.minecraft("adventure/avoid_vibration"),
			NamespacedKey.minecraft("adventure/sleep_in_bed"),
			NamespacedKey.minecraft("adventure/hero_of_the_village"),
			NamespacedKey.minecraft("adventure/spyglass_at_ghast"),
			NamespacedKey.minecraft("adventure/throw_trident"),
			NamespacedKey.minecraft("adventure/kill_mob_near_sculk_catalyst"),
			NamespacedKey.minecraft("adventure/shoot_arrow"),
			NamespacedKey.minecraft("adventure/kill_all_mobs"),
			NamespacedKey.minecraft("adventure/totem_of_undying"),
			NamespacedKey.minecraft("adventure/summon_iron_golem"),
			NamespacedKey.minecraft("adventure/trade_at_world_height"),
			NamespacedKey.minecraft("adventure/two_birds_one_arrow"),
			NamespacedKey.minecraft("adventure/whos_the_pillager_now"),
			NamespacedKey.minecraft("adventure/arbalistic"),
			NamespacedKey.minecraft("adventure/adventuring_time"),
			NamespacedKey.minecraft("adventure/play_jukebox_in_meadows"),
			NamespacedKey.minecraft("adventure/walk_on_powder_snow_with_leather_boots"),
			NamespacedKey.minecraft("adventure/spyglass_at_dragon"),
			NamespacedKey.minecraft("adventure/very_very_frightening"),
			NamespacedKey.minecraft("adventure/sniper_duel"),
			NamespacedKey.minecraft("adventure/bullseye")
		);
		
		server.addAdvancements(advancements);

		// /test
		assertEquals(advancements.stream().map(NamespacedKey::toString).sorted().toList(), server.getSuggestions(player, "test "));
		
		// /test adventure/a
		assertEquals(advancements.stream().map(NamespacedKey::toString).filter(key -> key.startsWith("minecraft:adventure/a")).sorted().toList(),
			server.getSuggestions(player, "test adventure/a"));
		
		// /test x
		assertEquals(List.of(), server.getSuggestions(player, "test x"));
	}

}
