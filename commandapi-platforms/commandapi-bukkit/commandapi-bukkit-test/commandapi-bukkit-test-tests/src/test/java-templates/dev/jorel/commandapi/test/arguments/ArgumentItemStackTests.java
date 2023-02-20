package dev.jorel.commandapi.test.arguments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.test.MockNMS;
import dev.jorel.commandapi.test.Mut;
import dev.jorel.commandapi.test.TestBase;

/**
 * Tests for the {@link ItemStackArgument}
 */
class ArgumentItemStackTests extends TestBase {

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

	@SuppressWarnings("deprecation")
	@Test
	void executionTestWithItemStackArgument() {
		Mut<ItemStack> results = Mut.of();

		new CommandAPICommand("test")
			.withArguments(new ItemStackArgument("item"))
			.executesPlayer((player, args) -> {
				results.set((ItemStack) args.get(0));
			})
			.register();

		PlayerMock player = server.addPlayer();

		// /test dirt
		server.dispatchCommand(player, "test dirt");
		assertEquals(new ItemStack(Material.DIRT), results.get());
		
		// Dev note: We're no longer using assertEquals on the ItemStack
		// object here because these itemstacks will now have ItemMeta
		// which has a reference to the NBT Compound Tag which we can't
		// perform equals against.
		
		// Dev note: To make these tests work, we have to overwrite MockBukkit's
		// ItemFactory with our own, see CommandAPIServerMock#getItemFactory()
		
		// NBT examples from https://minecraft.fandom.com/wiki/Tutorials/Command_NBT_tags#Items
		
		// /test minecraft:stone{Count:3b}
		{
			server.dispatchCommand(player, "test minecraft:stone{Count:3b}");
			ItemStack actual = results.get();
			assertEquals(Material.STONE, actual.getType());
			assertEquals(3, actual.getAmount());
		}
		
		// /test minecraft:diamond_sword{Enchantments:[{id:"minecraft:sharpness",lvl:1s}]}
		{
			server.dispatchCommand(player, "test minecraft:diamond_sword{Enchantments:[{id:\"minecraft:sharpness\",lvl:1s}]}");
			ItemStack actual = results.get();
			assertEquals(Material.DIAMOND_SWORD, actual.getType());
			assertEquals(1, actual.getAmount());
			assertEquals(1, actual.getEnchantmentLevel(Enchantment.DAMAGE_ALL));
		}
		
		// The example on the MinecraftWiki is wrong!
		// The MinecraftWiki has this:  {display:{Name:'{"text":"Tunic of Destiny","color":"blue",...}',Lore:['{"text":"A magical blue tunic"}','{"text":"worn by the gods..."}',color:3949738]}}
		// The actual (correct) NBT is: {display:{Name:'{"text":"Tunic of Destiny","color":"blue"}',Lore:['{"text":"A magical blue tunic"}','{"text":"worn by the gods..."}'],color:3949738}}
		// /test minecraft:leather_chestplate{display:{Name:'{"text":"Tunic of Destiny","color":"blue"}',Lore:['{"text":"A magical blue tunic"}','{"text":"worn by the gods..."}'],color:3949738}}
		{
			server.dispatchCommand(player, "test minecraft:leather_chestplate{display:{Name:'{\"text\":\"Tunic of Destiny\",\"color\":\"blue\"}',Lore:['{\"text\":\"A magical blue tunic\"}','{\"text\":\"worn by the gods...\"}'],color:3949738}}");
			ItemStack actual = results.get();
			ItemMeta actualMeta = actual.getItemMeta();
			assertEquals(Material.LEATHER_CHESTPLATE, actual.getType());
			assertEquals(1, actual.getAmount());
			assertEquals(ChatColor.BLUE + "Tunic of Destiny", actualMeta.getDisplayName());
			assertEquals(List.of("A magical blue tunic", "worn by the gods..."), actualMeta.getLore());
		}
		
		// /test minecraft:diamond_pickaxe{HideFlags:15}
		/* ${ifdef.1.17}
		{
			server.dispatchCommand(player, "test minecraft:diamond_pickaxe{HideFlags:15}");
			ItemStack actual = results.get();
			assertEquals(Material.DIAMOND_PICKAXE, actual.getType());
			assertEquals(1, actual.getAmount());
			assertTrue(actual.hasItemFlag(ItemFlag.HIDE_ENCHANTS));
			assertTrue(actual.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES));
			assertTrue(actual.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE));
			assertTrue(actual.hasItemFlag(ItemFlag.HIDE_DESTROYS));
		} /* */

		// /test firework_rocket{Fireworks:{Explosions:[{Type:3b,Flicker:1b,Colors:[I;4312372],FadeColors:[I;11743532]}],Flight:1b}}
		{
			server.dispatchCommand(player, "test firework_rocket{Fireworks:{Explosions:[{Type:3b,Flicker:1b,Colors:[I;4312372],FadeColors:[I;11743532]}],Flight:1b}}");
			ItemStack actual = results.get();
			ItemMeta actualMeta = actual.getItemMeta();
			assertEquals(Material.FIREWORK_ROCKET, actual.getType());
			assertEquals(1, actual.getAmount());
			assertInstanceOf(FireworkMeta.class, actualMeta);

			FireworkMeta fireworkMeta = (FireworkMeta) actualMeta;
			assertEquals(1, fireworkMeta.getEffectsSize());
			assertEquals(1, fireworkMeta.getPower());
			assertTrue(fireworkMeta.getEffects().get(0).hasFlicker());
			assertEquals(FireworkEffect.Type.CREEPER, fireworkMeta.getEffects().get(0).getType());
			assertEquals(Color.fromRGB(4312372), fireworkMeta.getEffects().get(0).getColors().get(0));      // LIME
			assertEquals(Color.fromRGB(11743532), fireworkMeta.getEffects().get(0).getFadeColors().get(0)); // RED
		}

		// /test item_that_doesnt_exist
		// Fails because "item_that_doesnt_exist" isn't a valid item
		assertCommandFailsWith(player, "test item_that_doesnt_exist", "Unknown item 'minecraft:item_that_doesnt_exist' at position 5: test <--[HERE]");
		
		// /test dirt{invalid_nbt}
		// Fails because "{invalid_nbt}" isn't valid NBT
		assertCommandFailsWith(player, "test dirt{invalid_nbt}", "Expected ':' at position 21: ...nvalid_nbt<--[HERE]");

		assertNoMoreResults(results);
	}

	/********************
	 * Suggestion tests *
	 ********************/

	@Test
	void suggestionTestWithItemStackArgument() {
		new CommandAPICommand("test")
			.withArguments(new ItemStackArgument("item"))
			.executesPlayer((player, args) -> {
			})
			.register();

		PlayerMock player = server.addPlayer();

		// /test
		// All items should be suggested
		assertEquals(MockNMS.getAllItemNames(), server.getSuggestions(player, "test "));

		// /test x
		// All items starting with 'a' should be suggested, as well as items which
		// are underscore-separated and start with 'a', such as 'wooden_axe'
		assertEquals(MockNMS.getAllItemNames().stream().filter(s -> s.contains(":a") || s.contains("_a")).toList(), server.getSuggestions(player, "test a"));
		
		// test dirt
		// Completed item names should suggest open brackets
		assertEquals(List.of("{"), server.getSuggestions(player, "test dirt"));
		
		// test dirt{
		// NBT has no suggestions
		assertEquals(List.of(), server.getSuggestions(player, "test dirt{"));
		
		// test dirt{}
		// NBT has no suggestions
		assertEquals(List.of(), server.getSuggestions(player, "test dirt{}"));
	}

}
