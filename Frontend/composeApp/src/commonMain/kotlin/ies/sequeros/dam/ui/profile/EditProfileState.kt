package ies.sequeros.dam.ui.profile


import ies.sequeros.dam.domain.models.Locality

data class EditProfileState(

    // Campos del formulario
    val name: String = "",
    val description: String = "",
    val locationId: Int? = null,
    val locationName: String = "",

    // Lista de provincias para el dropdown
    val localities: List<Locality> = emptyList(),
    val isLoadingLocalities: Boolean = false,

    // Imagen seleccionada aún no subida — bytes para vista previa local
    val previewBytes: ByteArray?  = null,
    val previewFileName: String?  = null,

    // Estados de las operaciones
    val isSaving: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val isPhotoSuccess: Boolean = false,

    // Errores
    val nameError: String? = null,
    val errorMessage: String? = null
) {
    /** El formulario solo se puede guardar si el nombre no está vacío y no hay errores */
    val isValid: Boolean
        get() = name.isNotBlank() && nameError == null
}