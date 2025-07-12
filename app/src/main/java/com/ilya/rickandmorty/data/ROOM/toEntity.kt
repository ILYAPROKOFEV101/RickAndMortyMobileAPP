package com.ilya.rickandmorty.data.ROOM

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ilya.rickandmorty.data.Character
import com.ilya.rickandmorty.data.Location
import com.ilya.rickandmorty.data.ROOM.Table.CharacterEntity

fun Character.toEntity():   CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        originName = origin.name,
        originUrl = origin.url,
        locationName = location.name,
        locationUrl = location.url,
        image = image,
        episodeJson = Gson().toJson(episode),
        url = url,
        created = created
    )
}

fun CharacterEntity.toCharacter(): Character {
    return Character(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = Location(originName, originUrl),
        location = Location(locationName, locationUrl),
        image = image,
        episode = Gson().fromJson(episodeJson, object : TypeToken<List<String>>() {}.type),
        url = url,
        created = created
    )
}
