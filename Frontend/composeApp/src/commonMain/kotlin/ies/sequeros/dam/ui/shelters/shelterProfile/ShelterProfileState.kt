package ies.sequeros.dam.ui.shelters.shelterProfile

import ies.sequeros.dam.domain.models.Post
import ies.sequeros.dam.domain.models.Shelter

data class ShelterProfileState(
    val shelter: Shelter? = null,
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingPosts: Boolean = false,
    val errorMessage: String? = null
)