package ies.sequeros.dam.ui.animals.animalEdit

data class AnimalEditState(
    val animalId: String = "",
    val name: String = "",
    val nameError: String? = null,
    val species: String = "Perro",
    val breed: String = "",
    val breedError: String? = null,
    val birthDate: String = "",
    val gender: String = "unknown",
    val size: String = "small",
    val description: String = "",
    val status: String = "available",
    val health: String = "",
    val profileImage: String? = null,
    val previewBytes: ByteArray? = null,
    val previewFileName: String? = null,
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val isPhotoSuccess: Boolean = false,
    val isDeleted: Boolean = false,
    val isUpdated: Boolean = false,
    val errorMessage: String? = null,
    val createdAnimalId: String? = null
) {
    val isCreateMode: Boolean get() = animalId.isEmpty()
    val isValid: Boolean get() = name.isNotBlank() && nameError == null
}
