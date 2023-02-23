package net.paladins.villager;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.runes.api.RuneItems;
import net.runes.crafting.RuneCraftingBlock;
import net.paladins.PaladinsMod;
import net.paladins.item.Armors;
import net.paladins.item.Weapons;
import net.paladins.item.PaladinArmor;

import java.util.List;

public class PaladinVillagers {
    public static final String PALADIN_MERCHANT = "paladin_merchant";

    public static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(new Identifier(PaladinsMod.ID, name),
                1, 10, ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = new Identifier(PaladinsMod.ID, name);
        return Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(PaladinsMod.ID, name), new VillagerProfession(
                id.toString(),
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                ImmutableSet.of(),
                ImmutableSet.of(),
                PaladinArmor.equipSound)
        );
    }

    private static class Offer {
        int level;
        ItemStack input;
        ItemStack output;
        int maxUses;
        int experience;
        float priceMultiplier;

        public Offer(int level, ItemStack input, ItemStack output, int maxUses, int experience, float priceMultiplier) {
            this.level = level;
            this.input = input;
            this.output = output;
            this.maxUses = maxUses;
            this.experience = experience;
            this.priceMultiplier = priceMultiplier;
        }

        public static Offer buy(int level, ItemStack item, int price, int maxUses, int experience, float priceMultiplier) {
            return new Offer(level, item, new ItemStack(Items.EMERALD, price), maxUses, experience, priceMultiplier);
        }

        public static Offer sell(int level, ItemStack item, int price, int maxUses, int experience, float priceMultiplier) {
            return new Offer(level, new ItemStack(Items.EMERALD, price), item, maxUses, experience, priceMultiplier);
        }
    }

    public static void register() {
        var paladinPOI = registerPOI(PALADIN_MERCHANT, RuneCraftingBlock.INSTANCE);
        var paladinMerchantProfession = registerProfession(
                PALADIN_MERCHANT,
                RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, new Identifier(PaladinsMod.ID, PALADIN_MERCHANT)));

        List<Offer> paladinMerchantOffers = List.of(
                Offer.sell(1, new ItemStack(RuneItems.get(RuneItems.RuneType.ARCANE), 8), 2, 128, 1, 0.01f),
                Offer.sell(1, new ItemStack(RuneItems.get(RuneItems.RuneType.FIRE), 8), 2, 128, 1, 0.01f),
                Offer.sell(1, new ItemStack(RuneItems.get(RuneItems.RuneType.FROST), 8), 2, 128, 1, 0.01f),
                Offer.sell(2, Weapons.noviceWand.item().getDefaultStack(), 4, 12, 5, 0.2f),
                Offer.sell(2, Weapons.arcaneWand.item().getDefaultStack(), 18, 12, 8, 0.1f),
                Offer.sell(2, Weapons.fireWand.item().getDefaultStack(), 18, 12, 8, 0.1f),
                Offer.sell(2, Weapons.frostWand.item().getDefaultStack(), 18, 12, 8, 0.1f),
                Offer.buy(2, new ItemStack(Items.WHITE_WOOL, 5), 8, 12, 10, 0.05f),
                Offer.buy(2, new ItemStack(Items.LAPIS_LAZULI, 6), 12, 12, 10, 0.05f),
                Offer.sell(3, Armors.paladinRobeSet.head.getDefaultStack(), 15, 12, 13, 0.1f),
                Offer.sell(3, Armors.paladinRobeSet.feet.getDefaultStack(), 15, 12, 13, 0.1f),
                Offer.sell(4, Armors.paladinRobeSet.chest.getDefaultStack(), 20, 12, 15, 0.1f),
                Offer.sell(4, Armors.paladinRobeSet.legs.getDefaultStack(), 20, 12, 15, 0.1f)
            );

        for(var offer: paladinMerchantOffers) {
            TradeOfferHelper.registerVillagerOffers(paladinMerchantProfession, offer.level, factories -> {
                factories.add(((entity, random) -> new TradeOffer(
                        offer.input,
                        offer.output,
                        offer.maxUses, offer.experience, offer.priceMultiplier)
                ));
            });
        }
        TradeOfferHelper.registerVillagerOffers(paladinMerchantProfession, 5, factories -> {
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.arcaneStaff.item(),
                    40,
                    3,
                    30,
                    0.2F).create(entity, random)
            ));
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.fireStaff.item(),
                    40,
                    3,
                    30,
                    0.2F).create(entity, random)
            ));
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.frostStaff.item(),
                    40,
                    3,
                    30,
                    0.2F).create(entity, random)
            ));
        });
    }
}
