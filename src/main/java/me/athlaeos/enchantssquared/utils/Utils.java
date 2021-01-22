package me.athlaeos.enchantssquared.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.util.*;

public class Utils {

	public static String chat (String s) {
		return ChatColor.translateAlternateColorCodes('&', s + "");
	}

	public static String toRoman(int i){
		switch(i){
			case 0: return "";
			case 1: return "I";
			case 2: return "II";
			case 3: return "III";
			case 4: return "IV";
			case 5: return "V";
			case 6: return "VI";
			case 7: return "VII";
			case 8: return "VIII";
			case 9: return "IX";
			case 10: return "X";
			case 11: return "XI";
			case 12: return "XII";
			case 13: return "XIII";
			case 14: return "XIV";
			case 15: return "XV";
			case 16: return "XVI";
			case 17: return "XVII";
			case 18: return "XVIII";
			case 19: return "XIX";
			case 20: return "XX";
			default: return "es.level." + i;
		}
	}

	public static int translateRomanToLevel(String i){
		switch(i){
			case "I": return 1;
			case "II": return 2;
			case "III": return 3;
			case "IV": return 4;
			case "V": return 5;
			case "VI": return 6;
			case "VII": return 7;
			case "VIII": return 8;
			case "IX": return 9;
			case "X": return 10;
			case "XI": return 11;
			case "XII": return 12;
			case "XIII": return 13;
			case "XIV": return 14;
			case "XV": return 15;
			case "XVI": return 16;
			case "XVII": return 17;
			case "XVIII": return 18;
			case "XIX": return 19;
			case "XX": return 20;
			default: {
				try{
					String s = i.replace("es.level.", "");
					return Integer.parseInt(ChatColor.stripColor(s));
				} catch (IllegalArgumentException e){
					return 0;
				}
			}
		}
	}

	public static List<Block> getNearbyBlocks3D(Location location, int radius, Material filter) {
		List<Block> blocks = new ArrayList<>();
		assert location.getWorld() != null;
		for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					Block b = location.getWorld().getBlockAt(x, y, z);
					if (b.getType() == filter || filter == null){
						blocks.add(b);
					}
				}
			}
		}
		return blocks;
	}

	public static List<Block> getNearbyBlocks2D(Location location, int radius, Material filter) {
		List<Block> blocks = new ArrayList<>();
		assert location.getWorld() != null;
		for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
				Block b = location.getWorld().getBlockAt(x, (int) location.getY(), z);
				if (b.getType() == filter || filter == null) {
					blocks.add(b);
				}
			}
		}
		return blocks;
	}

	public static Map<Integer, ArrayList<String>> pagesCreator(int pageSize, List<String> allEntries) {
		Map<Integer, ArrayList<String>> pages = new HashMap<Integer, ArrayList<String>>();
		int stepper = 0;

		for (int pageNumber = 0; pageNumber < Math.ceil((double)allEntries.size()/(double)pageSize); pageNumber++) {
			ArrayList<String> pageEntries = new ArrayList<String>();
			for (int pageEntry = 0; pageEntry < pageSize && stepper < allEntries.size(); pageEntry++, stepper++) {
				pageEntries.add(allEntries.get(stepper));
			}
			pages.put(pageNumber, pageEntries);
		}
		return pages;
	}

	public static List<Location> getBlocksInArea(Location loc1, Location loc2){
		List<Location> blocks = new ArrayList<Location>();

		int topBlockX = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int bottomBlockX = Math.min(loc1.getBlockX(), loc2.getBlockX());

		int topBlockY = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int bottomBlockY = Math.min(loc1.getBlockY(), loc2.getBlockY());

		int topBlockZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		int bottomBlockZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

		for(int x = bottomBlockX; x <= topBlockX; x++) {
			for(int z = bottomBlockZ; z <= topBlockZ; z++) {
				for(int y = bottomBlockY; y <= topBlockY; y++) {
					Location l = new Location(loc1.getWorld(), x, y, z);
					if (loc1.getWorld().getBlockAt(l).getType() != Material.AIR){
						blocks.add(l);
					}
				}
			}
		}
		return blocks;
	}

	public static double applyNaturalDamageMitigations(Entity damagee, double baseDamage, EntityDamageEvent.DamageCause cause){
		if (damagee instanceof LivingEntity){
			double armor = ((LivingEntity) damagee).getAttribute(Attribute.GENERIC_ARMOR).getValue();
			double armor_toughness = ((LivingEntity) damagee).getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
			int resistance = (((LivingEntity) damagee).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
					? ((LivingEntity) damagee).getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1 : 0;
			int epf = getEPF((LivingEntity) damagee, cause);

			double withArmorReduction = baseDamage * (1 - Math.min(20, Math.max(armor / 5, armor - baseDamage / (2 + armor_toughness / 4))) / 25);
			double withResistanceReduction = withArmorReduction * (1 - (resistance * 0.2));
			return withResistanceReduction * (1 - (Math.min(epf, 20) / 25D));
		} else {
			return baseDamage;
		}
	}

	public static double removeNaturalDamageMitigations(Entity damagee, double mitigatedDamage, EntityDamageEvent.DamageCause cause){
		if (damagee instanceof LivingEntity){
			double armor = ((LivingEntity) damagee).getAttribute(Attribute.GENERIC_ARMOR).getValue();
			double armor_toughness = ((LivingEntity) damagee).getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
			int resistance = (((LivingEntity) damagee).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
					? ((LivingEntity) damagee).getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1 : 0;
			int epf = getEPF((LivingEntity) damagee, cause);

			double withoutEnchantmentReduction = mitigatedDamage / (1 - (Math.min(epf, 20) / 25D));
			double withoutResistanceReduction = withoutEnchantmentReduction / (1 - (resistance * 0.2));
			double withoutArmorReduction = withoutResistanceReduction / (1 - Math.min(20, Math.max(armor / 5, armor - mitigatedDamage / (2 + armor_toughness / 4))) / 25);
			return withoutArmorReduction;
		} else {
			return mitigatedDamage;
		}
	}

	public static Map<Integer, ArrayList<String>> paginateTextList(int pageSize, List<String> allEntries) {
		Map<Integer, ArrayList<String>> pages = new HashMap<>();
		int stepper = 0;

		for (int pageNumber = 0; pageNumber < Math.ceil((double)allEntries.size()/(double)pageSize); pageNumber++) {
			ArrayList<String> pageEntries = new ArrayList<>();
			for (int pageEntry = 0; pageEntry < pageSize && stepper < allEntries.size(); pageEntry++, stepper++) {
				pageEntries.add(allEntries.get(stepper));
			}
			pages.put(pageNumber, pageEntries);
		}
		return pages;
	}

	public static List<ItemStack> getEntityEquipment(Entity entity, boolean getHands){
		List<ItemStack> equipment = new ArrayList<>();
		if (entity == null) return new ArrayList<>();
		if (!(entity instanceof LivingEntity)) return equipment;
		LivingEntity e = (LivingEntity) entity;
		if (e.getEquipment() != null) {
			if (e.getEquipment().getHelmet() != null){ equipment.add(e.getEquipment().getHelmet()); }
			if (e.getEquipment().getChestplate() != null){ equipment.add(e.getEquipment().getChestplate()); }
			if (e.getEquipment().getLeggings() != null){ equipment.add(e.getEquipment().getLeggings()); }
			if (e.getEquipment().getBoots() != null){ equipment.add(e.getEquipment().getBoots()); }
			if (getHands){
				if (e.getEquipment().getItemInMainHand().getType() != Material.AIR){ equipment.add(e.getEquipment().getItemInMainHand()); }
				if (e.getEquipment().getItemInOffHand().getType() != Material.AIR){ equipment.add(e.getEquipment().getItemInOffHand()); }
			}
		}
		return equipment;
	}

	public static int getEPF(LivingEntity entity, EntityDamageEvent.DamageCause cause){
		int epf = 0;
		for (ItemStack i : getEntityEquipment(entity, false)){
			if (cause == EntityDamageEvent.DamageCause.PROJECTILE){
				epf += 2 * i.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE);
			}
			if (Arrays.asList(
					EntityDamageEvent.DamageCause.FIRE,
					EntityDamageEvent.DamageCause.LAVA,
					EntityDamageEvent.DamageCause.FIRE_TICK,
					EntityDamageEvent.DamageCause.HOT_FLOOR,
					EntityDamageEvent.DamageCause.MELTING).contains(cause)){
				epf += 2 * i.getEnchantmentLevel(Enchantment.PROTECTION_FIRE);
			}
			if (Arrays.asList(
					EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
					EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
			).contains(cause)){
				epf += 2 * i.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS);
			}
			if (Arrays.asList(
					EntityDamageEvent.DamageCause.FALL,
					EntityDamageEvent.DamageCause.FLY_INTO_WALL
			).contains(cause)){
				epf += 3 * i.getEnchantmentLevel(Enchantment.PROTECTION_FALL);
			}
			epf += i.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
		}
		return epf;
	}

	public static List<Location> getPointsInLine(Location point1, Location point2, int amount){
		double xStep = (point1.getX() - point2.getX()) / amount;
		double yStep = (point1.getY() - point2.getY()) / amount;
		double zStep = (point1.getZ() - point2.getZ()) / amount;
		List<Location> points = new ArrayList<>();
		for (int i = 0; i < amount + 1; i++){
			points.add(new Location(
					point1.getWorld(),
					point1.getX() - xStep * i,
					point1.getY() - yStep * i,
					point1.getZ() - zStep * i));
		}
		return points;
	}
}
