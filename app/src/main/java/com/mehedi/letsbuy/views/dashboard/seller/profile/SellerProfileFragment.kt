package com.mehedi.letsbuy.views.dashboard.seller.profile

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.mehedi.letsbuy.base.BaseFragment
import com.mehedi.letsbuy.core.DataState
import com.mehedi.letsbuy.core.areAllPermissionsGranted
import com.mehedi.letsbuy.core.extract
import com.mehedi.letsbuy.core.requestPermissions
import com.mehedi.letsbuy.databinding.FragmentSellerProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerProfileFragment :
    BaseFragment<FragmentSellerProfileBinding>(FragmentSellerProfileBinding::inflate) {

    private var sellerProfile: Profile? = null

    private val viewModel: SellerProfileViewModel by viewModels()

    private var hasLocalImageUrl: Boolean = false

    override fun setListener() {

        FirebaseAuth.getInstance().currentUser?.let {

            viewModel.getUserByUserID(it.uid)
        }

        permissionsRequest = getPermissionsRequest()
        binding.apply {
            ivProfile.setOnClickListener {
                requestPermissions(permissionsRequest, permissionList)
            }

            btnUpdate.setOnClickListener {

                loading.show()
                val name = etName.extract()
                val email = etEmail.extract()


                sellerProfile.apply {
                    this?.name = name
                    this?.email = email
                }

                updateProfile(sellerProfile)

            }

        }


    }

    private fun updateProfile(sellerProfile: Profile?) {
        sellerProfile?.let { viewModel.updateProfile(it, hasLocalImageUrl) }
    }


    override fun allObserver() {


        viewModel.profileUpdateResponse.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Error -> {
                    loading.dismiss()
                }

                is DataState.Loading -> {
                    loading.show()
                }

                is DataState.Success -> {
                    Toast.makeText(requireContext(), it.data, Toast.LENGTH_LONG).show()

                    loading.dismiss()
                }
            }
        }


        viewModel.logedInUserResponse.observe(viewLifecycleOwner) {

            when (it) {
                is DataState.Error -> {
                    loading.dismiss()
                }

                is DataState.Loading -> {
                    loading.show()
                }

                is DataState.Success -> {
                    sellerProfile = it.data
                    setProfileData(sellerProfile)

                    loading.dismiss()
                }
            }


        }


    }

    private fun setProfileData(sellerProfile: Profile?) {

        hasLocalImageUrl = sellerProfile?.userImage.isNullOrBlank()

        binding.apply {
            etName.setText(sellerProfile?.name)
            etEmail.setText(sellerProfile?.email)
            ivProfile.load(sellerProfile?.userImage)


        }


    }

    companion object {
        private val permissionList = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )
    }

    private lateinit var permissionsRequest: ActivityResultLauncher<Array<String>>

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    Log.d("TAG", "$fileUri")
                    binding.ivProfile.setImageURI(fileUri)
                    sellerProfile?.userImage = fileUri.toString()
                    hasLocalImageUrl = true


                }

                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun getPermissionsRequest(): ActivityResultLauncher<Array<String>> {

        return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (areAllPermissionsGranted(permissionList)) {


                // ase

                Toast.makeText(requireContext(), "Ase", Toast.LENGTH_SHORT).show()


                ImagePicker.with(this)
                    .compress(1024)         //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        512,
                        512
                    )  //Final image resolution will be less than 1080 x 1080(Optional)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }

            } else {
                // nai
                Toast.makeText(requireContext(), "Ase", Toast.LENGTH_SHORT).show()

            }
        }
    }


}