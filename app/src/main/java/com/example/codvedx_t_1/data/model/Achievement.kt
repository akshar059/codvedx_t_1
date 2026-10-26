package com.example.codvedx_t_1.data.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val progress: Int,
    val maxProgress: Int
)
