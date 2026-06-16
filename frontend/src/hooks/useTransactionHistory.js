import { useEffect, useState, useCallback } from 'react'
import api from '../api/axios'

export function useTransactionHistory() {
  const [history, setHistory] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [historyError, setHistoryError] = useState('')

  const fetchHistory = useCallback(async (pageNumber) => {
    try {
      setHistoryError('')
      const response = await api.get(`/transactions/history?page=${pageNumber}&size=3`)
      setHistory(response.data.content)
      setTotalPages(response.data.totalPages)
    } catch {
      setHistoryError('No se pudo cargar el historial. Intentá de nuevo.')
    }
  }, [])

  useEffect(() => {
    fetchHistory(page)
  }, [page, fetchHistory])

  return { history, page, setPage, totalPages, historyError }
}