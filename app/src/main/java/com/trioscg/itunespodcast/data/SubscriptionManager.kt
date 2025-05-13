package com.trioscg.itunespodcast.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trioscg.itunespodcast.data.Podcast

object SubscriptionManager {
    private const val PREFS_NAME = "subscriptions_prefs"
    private const val KEY_SUBSCRIPTIONS = "subscriptions"
    private val gson = Gson()

    fun getSubscriptions(context: Context): List<Podcast> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SUBSCRIPTIONS, "[]") ?: "[]"
        val type = object : TypeToken<List<Podcast>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addSubscription(context: Context, podcast: Podcast) {
        val current = getSubscriptions(context).toMutableList()
        if (current.none { it.trackId == podcast.trackId }) {
            current.add(podcast)
            saveSubscriptions(context, current)
        }
    }

    fun removeSubscription(context: Context, podcastId: Long) {
        val updated = getSubscriptions(context).filter { it.trackId != podcastId }
        saveSubscriptions(context, updated)
    }

    fun isSubscribed(context: Context, podcastId: Long): Boolean {
        return getSubscriptions(context).any { it.trackId == podcastId }
    }

    private fun saveSubscriptions(context: Context, subscriptions: List<Podcast>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SUBSCRIPTIONS, gson.toJson(subscriptions)).apply()
    }
}
