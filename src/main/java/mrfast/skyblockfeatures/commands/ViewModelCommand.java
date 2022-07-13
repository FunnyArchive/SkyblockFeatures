/*
 * skyblockfeatures - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2021 skyblockfeatures
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mrfast.skyblockfeatures.commands;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.utils.GuiUtil;
import mrfast.skyblockfeatures.features.impl.ItemFeatures.ViewModel;

public class ViewModelCommand extends Command {

    public ViewModelCommand() {
        super("vm");
    }
    
    @DefaultHandler
    public void handle() {
        GuiUtil.open(Objects.requireNonNull(new ViewModel.AuctionPriceScreen()));
    }

    @Nullable
    @Override
    public Set<Alias> getCommandAliases() {
        return Collections.singleton(new Alias("vm"));
    }
}