package com.example.piec_1.data.repository

import android.content.Context
import com.example.piec_1.data.local.AppDatabase
import com.example.piec_1.data.remote.ApiClient
import com.example.piec_1.data.local.daos.ScanQueueDao
class ScanRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val scanDao = db.scanQueueDao()

    // Agora pegamos a instância de dentro do seu ApiClient
    private val apiClient = ApiClient()
    val apiService = apiClient.apiService

    suspend fun getPendingScans() = scanDao.getPendingScans()

    suspend fun insertScanQueue(item: com.example.piec_1.data.local.entity.ScanQueueItem) {
        scanDao.insert(item)
    }

    suspend fun updateStatus(id: Int, status: String) {
        scanDao.updateStatus(id, status)
    }
}