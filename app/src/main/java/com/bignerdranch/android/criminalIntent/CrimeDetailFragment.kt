package com.bignerdranch.android.criminalIntent

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.text.format.DateFormat.format
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bignerdranch.android.criminalIntent.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.io.File
import java.util.*

private const val TAG = "CrimeDetailFragment"
private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeDetailFragment : Fragment(){

    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }

    private val selectSuspect = registerForActivityResult(ActivityResultContracts.PickContact()){
        uri: Uri? -> uri?.let { parseContractSelection(it) }
    }

    //chapter17. 사진찍기 함수
    //takePicture함수를 call해서 result로 받아오는데,
    //photo가 존재하고, photoName이 존재하면, viewModel을 업데이트하기
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()){
        didTakePhoto: Boolean ->
        // 결과값 처리
        if(didTakePhoto && photoName!=null){
            crimeDetailViewModel.updateCrime {
                oldCrime -> oldCrime.copy(photoFileName = photoName)
            }
        }
    }

    //chapter17. 사진파일 이름
    private val photoName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crime = Crime(
            id = UUID.randomUUID(),
            title = "",
            date = Date(),
            isSolved = false
        )
        Log.d(TAG, "The crime is: ${args.crimeId}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentCrimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }

            crimeSuspect.setOnClickListener {
                selectSuspect.launch(null)
            }

            val selectSuspectIntent = selectSuspect.contract.createIntent(requireContext(),null)

            crimeSuspect.isEnabled = canResolveIntent(selectSuspectIntent)

            // chapter17. 사진파일 이름, 사진파일, 사진파일 Uri 지정한 다음에 photoUri를 intent에 넣어서 takePhoto 함수 launch
            crimeCamera.setOnClickListener{
                val photoName = "IMG_${Date()}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName)
                val photoUri = FileProvider.getUriForFile(requireContext(), "com.bignerdranch.android.criminalIntent.fileprovider", photoFile)

                takePhoto.launch(photoUri)

                // chapter17. takePhoto에서 image(사진)를 가져와서 화면에 표시
                // query declaration 필요
                // 이부분 확인 필요
                val captureImageIntent = takePhoto.contract.createIntent(requireContext(), null)
                crimeCamera.isEnabled = canResolveIntent(captureImageIntent)
            }

            // chapter17. takePhoto에서 image(사진)를 가져와서 화면에 표시
            // query declaration 필요
            // 이부분 확인 필요
//            val captureImageIntent = takePhoto.contract.createIntent(requireContext(), null)
//            crimeCamera.isEnabled = canResolveIntent(captureImageIntent)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let { updateUi(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate =
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }
    }

    private fun updateUi(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeDate.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectDate(crime.date)
                )
            }

            crimeSolved.isChecked = crime.isSolved

            crimeReport.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                }

                startActivity(reportIntent)
                val chooserIntent = Intent.createChooser(reportIntent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }

            crimeSuspect.text = crime.suspect.ifEmpty {
                getString(R.string.crime_suspect_text)
            }
            updatePhoto(crime.photoFileName)
        }
    }

    private fun getCrimeReport(crime: Crime): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspectText = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspectText)
    }

    //연락처 선택(choose suspect에 연락처 이름 뜸)
    private fun parseContractSelection(contractUri: Uri){
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val queryCursor = requireActivity().contentResolver.query(contractUri, queryFields, null, null, null)

        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()){
                val suspect = cursor.getString(0)
                crimeDetailViewModel.updateCrime {
                    oldCrime -> oldCrime.copy(suspect = suspect)
                }
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean{

        //연락처 선택기능 삭제 코드(주석)
//        intent.addCategory(Intent.CATEGORY_HOME)

        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

        return resolvedActivity != null
    }

    // chapter17. 사진크기 scaling 함수 호출
    private fun updatePhoto(photoFileName: String?){
        if(binding.crimePhoto.tag != photoFileName){
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true){
                binding.crimePhoto.doOnLayout{
                    measuredView -> val scaledBitmap = getScaledBitmap(photoFile.path, measuredView.width, measuredView.height)
                    binding.crimePhoto.setImageBitmap(scaledBitmap)
                    binding.crimePhoto.tag = photoFileName
                }
            } else{
                binding.crimePhoto.setImageBitmap(null)
                binding.crimePhoto.tag = null
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}