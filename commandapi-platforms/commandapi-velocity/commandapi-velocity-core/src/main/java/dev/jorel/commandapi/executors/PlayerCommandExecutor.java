/*******************************************************************************
 * Copyright 2018, 2020 Jorel Ali (Skepter) - MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package dev.jorel.commandapi.executors;

import com.velocitypowered.api.proxy.Player;
import dev.jorel.commandapi.commandsenders.VelocityPlayer;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;

/**
 * A normal command executor for a Player
 */
@FunctionalInterface
public interface PlayerCommandExecutor extends NormalExecutor<Player, VelocityPlayer> {

	/**
	 * The code to run when this command is performed
	 *
	 * @param sender The sender of this command (a player, the console etc.)
	 * @param args The arguments given to this command.
	 */
	void run(Player sender, CommandArguments args) throws WrapperCommandSyntaxException;

	/**
	 * The code to run when this command is performed
	 *
	 * @param info The ExecutionInfo for this command
	 */
	@Override
	default void run(ExecutionInfo<Player, VelocityPlayer> info) throws WrapperCommandSyntaxException {
		this.run(info.sender(), info.args());
	}

	/**
	 * Returns the type of the sender of the current executor.
	 * @return the type of the sender of the current executor
	 */
	@Override
	default ExecutorType getType() {
		return ExecutorType.PLAYER;
	}
}
