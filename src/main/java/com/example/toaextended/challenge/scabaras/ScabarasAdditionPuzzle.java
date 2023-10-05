/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2023, rdutta <https://github.com/rdutta>
 * Copyright (c) 2022, LlemonDuck
 * Copyright (c) 2022, TheStonedTurtle
 * Copyright (c) 2019, Ron Young <https://github.com/raiyni>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.example.toaextended.challenge.scabaras;

import com.example.toaextended.ToaExtendedConfig;
import com.example.toaextended.module.PluginLifecycleComponent;
import com.example.toaextended.util.RaidRoom;
import com.example.toaextended.util.RaidState;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ScabarasAdditionPuzzle implements PluginLifecycleComponent
{

	@RequiredArgsConstructor
	@Getter(AccessLevel.PACKAGE)
	enum AdditionTile
	{
		LINE(1, 45345, 45388),
		KNIVES(2, 45346, 45389),
		TRIANGLE(3, 45347, 45390),
		DIAMOND(4, 45348, 45391),
		HAND(5, 45349, 45392),
		BIRD(6, 45350, 45393),
		CROOK(7, 45351, 45386),
		WIGGLE(8, 45352, 45394),
		FOOT(9, 45353, 45395),
		;

		private final int value;
		private final int groundObjectId;
		private final int gameObjectId;
	}

	private static final Set<Integer> GAME_OBJECT_IDS = Arrays.stream(AdditionTile.values())
		.map(AdditionTile::getGameObjectId)
		.collect(Collectors.toSet());

	private static final Point[] SCENE_COORD_STARTS = {
		new Point(36, 56),
		new Point(36, 44),
		new Point(53, 56),
		new Point(53, 44),
	};


	private static final Map<Integer, Set<Integer>> OPTIMAL_SOLUTIONS = ImmutableMap.<Integer, Set<Integer>>builder()
		.put(20, ImmutableSet.of(5, 11, 17))
		.put(21, ImmutableSet.of(10, 11, 17))
		.put(22, ImmutableSet.of(10, 11, 12, 18, 24))
		.put(23, ImmutableSet.of(5, 6, 7, 8, 14))
		.put(24, ImmutableSet.of(5, 11, 17, 23))
		.put(25, ImmutableSet.of(10, 11, 12, 13))
		.put(26, ImmutableSet.of(9, 10, 11, 12, 13))
		.put(27, ImmutableSet.of(5, 6, 7, 8, 4))
		.put(28, ImmutableSet.of(0, 1, 7, 13, 19))
		.put(29, ImmutableSet.of(10, 11, 12, 13, 19))
		.put(30, ImmutableSet.of(10, 11, 12, 13, 14))
		.put(31, ImmutableSet.of(0, 6, 12, 13, 14))
		.put(32, ImmutableSet.of(2, 3, 4, 6, 10))
		.put(33, ImmutableSet.of(4, 5, 6, 7, 8, 9, 14))
		.put(34, ImmutableSet.of(10, 11, 12, 13, 14, 19))
		.put(35, ImmutableSet.of(9, 10, 11, 12, 13, 14, 19))
		.put(36, ImmutableSet.of(0, 1, 2, 3, 4, 9, 14))
		.put(37, ImmutableSet.of(10, 11, 12, 13, 14, 19, 24))
		.put(38, ImmutableSet.of(0, 5, 6, 10, 12, 18, 24))
		.put(39, ImmutableSet.of(2, 3, 4, 7, 10, 11, 12))
		.put(40, ImmutableSet.of(4, 9, 10, 11, 12, 13, 14))
		.put(41, ImmutableSet.of(0, 4, 6, 9, 12, 13, 14))
		.put(42, ImmutableSet.of(0, 5, 9, 10, 11, 12, 13))
		.put(43, ImmutableSet.of(0, 1, 5, 7, 10, 13, 19))
		.put(44, ImmutableSet.of(0, 5, 10, 11, 14, 17, 18))
		.put(45, ImmutableSet.of(0, 1, 2, 3, 4, 5, 10))
		.build();

	private static final Pattern TARGET_NUMBER_PATTERN = Pattern.compile("The number (\\d+) has been hastily chipped into the stone.");

	private final EventBus eventBus;
	private final Client client;

	private boolean solved;
	private Set<Integer> tileStates;
	private int targetNumber;

	@Getter(AccessLevel.PACKAGE)
	private Set<LocalPoint> flips = Collections.emptySet();

	@Override
	public boolean isEnabled(final ToaExtendedConfig config, final RaidState raidState)
	{
		return raidState.getCurrentRoom() == RaidRoom.SCABARAS && config.scabarasAdditionPuzzle();
	}

	@Override
	public void startUp()
	{
		eventBus.register(this);

		this.targetNumber = 0;
		solved = false;
	}

	@Override
	public void shutDown()
	{
		eventBus.unregister(this);
	}

	@Subscribe
	public void onGameObjectSpawned(final GameObjectSpawned event)
	{
		final GameObject gameObject = event.getGameObject();

		final int id = gameObject.getId();

		if (GAME_OBJECT_IDS.contains(id))
		{
			solved = false;
		}
	}

	@Subscribe
	public void onGameObjectDespawned(final GameObjectDespawned event)
	{
		final GameObject gameObject = event.getGameObject();

		final int id = gameObject.getId();

		if (GAME_OBJECT_IDS.contains(id))
		{
			solved = false;
		}
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		if (!solved)
		{
			solve();
		}
	}

	@Subscribe
	public void onChatMessage(final ChatMessage event)
	{
		if (event.getMessage().startsWith("Your party failed to complete the challenge"))
		{
			this.targetNumber = 0;
			solved = false;
			return;
		}

		final Matcher matcher = TARGET_NUMBER_PATTERN.matcher(Text.removeTags(event.getMessage()));
		if (!matcher.matches())
		{
			return;
		}

		this.targetNumber = Integer.parseInt(matcher.group(1));
		solve();
	}

	private void solve()
	{
		solved = true;
		if (this.targetNumber < 20)
		{
			return;
		}

		final Tile[][] sceneTiles = client.getScene().getTiles()[client.getPlane()];
		final Point tl = findStartTile(sceneTiles);
		if (tl == null)
		{
			log.warn("Failed to locate start of addition puzzle");
			return;
		}

		this.tileStates = readTileStates(sceneTiles, tl);
		this.flips = findSolution(tl);
	}

	private Point findStartTile(final Tile[][] sceneTiles)
	{
		for (final Point sceneCoordStart : SCENE_COORD_STARTS)
		{
			final Tile startTile = sceneTiles[sceneCoordStart.getX()][sceneCoordStart.getY()];
			final GroundObject groundObject = startTile.getGroundObject();
			if (groundObject != null && groundObject.getId() == AdditionTile.FOOT.getGroundObjectId())
			{
				return sceneCoordStart;
			}
		}

		return null;
	}

	private Set<Integer> readTileStates(final Tile[][] sceneTiles, final Point topLeft)
	{
		final Set<Integer> tileStates = new HashSet<>();
		for (int y = 0; y < 5; y++)
		{
			for (int x = 0; x < 5; x++)
			{
				final Tile additionTile = sceneTiles[topLeft.getX() + x][topLeft.getY() - y];
				final boolean active = Arrays.stream(additionTile.getGameObjects())
					.filter(Objects::nonNull)
					.mapToInt(GameObject::getId)
					.anyMatch(GAME_OBJECT_IDS::contains);

				if (active)
				{
					tileStates.add(y * 5 + x);
				}
			}
		}

		return tileStates;
	}

	private Set<LocalPoint> findSolution(final Point topLeft)
	{
		final Set<Integer> remaining = Sets.difference(OPTIMAL_SOLUTIONS.get(targetNumber), this.tileStates);

		return remaining.stream()
			.map(i -> LocalPoint.fromScene(topLeft.getX() + i % 5, topLeft.getY() - i / 5))
			.collect(Collectors.toSet());
	}

}
