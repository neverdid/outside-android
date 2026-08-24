package com.neverdid.outside.data

import com.neverdid.outside.model.Activity
import com.neverdid.outside.model.ActivityAccent
import com.neverdid.outside.model.ActivityCategory
import com.neverdid.outside.model.ChatMessage
import com.neverdid.outside.model.Conversation
import com.neverdid.outside.model.FeedPost
import com.neverdid.outside.model.ForumTopic

object SampleData {
    val activities = listOf(
        Activity(
            id = "sunset-trail",
            title = "Sunset trail & tea",
            category = ActivityCategory.HIKING,
            host = "Mara Ionescu",
            hostInitials = "MI",
            day = "TODAY",
            date = "24 AUG",
            time = "18:30",
            location = "Tâmpa trailhead",
            distance = "2.4 km away",
            going = 6,
            capacity = 8,
            vibe = "Easy pace · Newcomers welcome",
            description = "A relaxed climb before sunset, then tea at the viewpoint. We will stay together and take plenty of breaks.",
            bring = listOf("Water", "Light jacket", "Headlamp"),
            accent = ActivityAccent.FOREST,
        ),
        Activity(
            id = "morning-run",
            title = "No-pressure 5K",
            category = ActivityCategory.RUNNING,
            host = "Andrei Pop",
            hostInitials = "AP",
            day = "TOMORROW",
            date = "25 AUG",
            time = "07:15",
            location = "Parcul Central",
            distance = "1.1 km away",
            going = 4,
            capacity = 10,
            vibe = "6:30–7:00/km · Social run",
            description = "An easy loop for people who want a reason to get out before work. Nobody gets left behind.",
            bring = listOf("Running shoes", "Water"),
            accent = ActivityAccent.SUNSET,
        ),
        Activity(
            id = "lake-paddle",
            title = "First-time paddle crew",
            category = ActivityCategory.CASUAL,
            host = "Sofia Marin",
            hostInitials = "SM",
            day = "SATURDAY",
            date = "29 AUG",
            time = "10:00",
            location = "Noua Lake dock",
            distance = "4.8 km away",
            going = 5,
            capacity = 6,
            vibe = "Beginner · Gear available",
            description = "Trying paddle boarding together. The rental desk has boards and life jackets, so curiosity is the only requirement.",
            bring = listOf("Towel", "Sunscreen", "Change of clothes"),
            accent = ActivityAccent.LAKE,
        ),
        Activity(
            id = "campfire-weekend",
            title = "Tiny campfire weekend",
            category = ActivityCategory.CAMPING,
            host = "Radu & Ioana",
            hostInitials = "R&I",
            day = "NEXT WEEK",
            date = "05 SEP",
            time = "16:00",
            location = "Zărnești meadow",
            distance = "27 km away",
            going = 7,
            capacity = 12,
            vibe = "One night · Shared cooking",
            description = "A friendly one-night camp with a shared dinner and an optional morning walk. Great for first-time campers.",
            bring = listOf("Tent", "Sleeping bag", "Cup"),
            accent = ActivityAccent.VIOLET,
        ),
    )

    val feedPosts = listOf(
        FeedPost(
            id = "post-1",
            author = "Mara Ionescu",
            initials = "MI",
            timeAgo = "32 min",
            text = "Found the quietest viewpoint above the city. Adding this stop to tonight’s trail ✨",
            activityLabel = "HIKING · TÂMPA",
            reactions = 24,
            comments = 6,
            accent = ActivityAccent.FOREST,
        ),
        FeedPost(
            id = "post-2",
            author = "No-pressure runners",
            initials = "5K",
            timeAgo = "2 hr",
            text = "Four strangers at 7 AM, four friends by breakfast. Same easy loop next Tuesday?",
            activityLabel = "RUNNING · PARCUL CENTRAL",
            reactions = 41,
            comments = 12,
            accent = ActivityAccent.SUNSET,
        ),
        FeedPost(
            id = "post-3",
            author = "Sofia Marin",
            initials = "SM",
            timeAgo = "Yesterday",
            text = "Does anyone want to learn paddle boarding badly and gracefully with me this weekend?",
            activityLabel = "LOOKING FOR PEOPLE · BEGINNER",
            reactions = 18,
            comments = 9,
            accent = ActivityAccent.LAKE,
        ),
    )

    val topics = listOf(
        ForumTopic(
            id = "topic-1",
            title = "Best beginner hikes reachable without a car?",
            category = "HIKING",
            author = "Elena D.",
            preview = "I’m new in town and relying on buses. Looking for 2–4 hour routes…",
            replies = 18,
            timeAgo = "12 min",
            isHot = true,
        ),
        ForumTopic(
            id = "topic-2",
            title = "Tent repair meetup — I have patches and zero expertise",
            category = "CAMPING",
            author = "Victor N.",
            preview = "Maybe we can fix our gear together at the park this Thursday?",
            replies = 7,
            timeAgo = "1 hr",
        ),
        ForumTopic(
            id = "topic-3",
            title = "A friendly cycling route for nervous road riders",
            category = "CYCLING",
            author = "Ana C.",
            preview = "Sharing the low-traffic route that helped me get comfortable outside…",
            replies = 22,
            timeAgo = "3 hr",
            isHot = true,
        ),
        ForumTopic(
            id = "topic-4",
            title = "What do you wish you knew before your first climb?",
            category = "CLIMBING",
            author = "Paul R.",
            preview = "Let’s build a no-judgment checklist for people trying the gym.",
            replies = 31,
            timeAgo = "Yesterday",
        ),
    )

    val conversations = listOf(
        Conversation(
            id = "mara",
            name = "Mara Ionescu",
            initials = "MI",
            preview = "See you at the trailhead!",
            time = "18:08",
            activity = "Sunset trail & tea",
        ),
        Conversation(
            id = "sunset-group",
            name = "Sunset trail & tea",
            initials = "🥾",
            preview = "Mara: I’ll bring an extra thermos!",
            time = "18:04",
            unread = 3,
            isGroup = true,
            activity = "Today · 18:30",
        ),
        Conversation(
            id = "sofia",
            name = "Sofia Marin",
            initials = "SM",
            preview = "Perfect, see you by the rental desk.",
            time = "16:42",
            unread = 1,
            activity = "First-time paddle crew",
        ),
        Conversation(
            id = "andrei",
            name = "Andrei Pop",
            initials = "AP",
            preview = "Easy pace works for me too 👍",
            time = "Yesterday",
        ),
        Conversation(
            id = "camp-group",
            name = "Tiny campfire weekend",
            initials = "⛺",
            preview = "Ioana: Poll for shared dinner is up",
            time = "Mon",
            isGroup = true,
            activity = "5 Sep · 16:00",
        ),
    )

    val messages = mapOf(
        "mara" to listOf(
            ChatMessage("m1", "You", "Hi Mara — is this a good first hike?", "17:44", true),
            ChatMessage("m2", "Mara", "Definitely. We’ll take it easy and stay together.", "17:48", false),
            ChatMessage("m3", "Mara", "See you at the trailhead!", "18:08", false),
        ),
        "sunset-group" to listOf(
            ChatMessage("m1", "Mara", "Hey everyone! Trail conditions look great.", "17:51", false),
            ChatMessage("m2", "Andrei", "Nice. Is the first section muddy?", "17:55", false),
            ChatMessage("m3", "You", "I can bring a spare headlamp if anyone needs one.", "17:58", true),
            ChatMessage("m4", "Mara", "That would be wonderful. I’ll bring an extra thermos!", "18:04", false),
        ),
        "sofia" to listOf(
            ChatMessage("m1", "Sofia", "Hi! Have you paddle boarded before?", "16:31", false),
            ChatMessage("m2", "You", "Never — but I’m happy to fall in a few times.", "16:36", true),
            ChatMessage("m3", "Sofia", "Same energy here 😄 Perfect, see you by the rental desk.", "16:42", false),
        ),
        "andrei" to listOf(
            ChatMessage("m1", "You", "Is the run beginner friendly?", "Yesterday", true),
            ChatMessage("m2", "Andrei", "Absolutely. Easy pace works for me too 👍", "Yesterday", false),
        ),
    )
}
