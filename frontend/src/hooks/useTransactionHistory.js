import { useEffect, useState, useCallback } from 'react'
import api from '../api/axios'

export function useTransactionHistory({ size = 3, type = '' } = {}) {
  const [history, setHistory] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [historyError, setHistoryError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const fetchHistory = useCallback(async (pageNumber) => {
    try {
      setIsLoading(true)
      setHistoryError('')
      const params = new URLSearchParams({ page: pageNumber, size })
      if (type) params.append('type', type)
      const response = await api.get(`/transactions/history?${params}`)
      setHistory(response.data.content)
      setTotalPages(response.data.totalPages)
    } catch {
      setHistoryError('No se pudo cargar el historial. Intentá de nuevo.')
    } finally {
      setIsLoading(false)
    }
  }, [size, type])

  useEffect(() => {
    setPage(0)
  }, [type])

  useEffect(() => {
    fetchHistory(page)
  }, [page, fetchHistory])

  return { history, page, setPage, totalPages, historyError, isLoading }
}