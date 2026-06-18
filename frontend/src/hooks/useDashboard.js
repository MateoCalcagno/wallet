import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'

export function useDashboard() {
  const [user, setUser] = useState(null)
  const [balance, setBalance] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [meRes, balanceRes] = await Promise.all([
          api.get('/users/me'),
          api.get('/wallet/me')
        ])
        setUser(meRes.data)
        setBalance(balanceRes.data.balance)
      } catch (err) {
        navigate('/login')
      } finally {
        setIsLoading(false)
      }
    }

    fetchData()
  }, [navigate])

  const handleLogout = () => {
    localStorage.removeItem('token')
    navigate('/login')
  }

  return { user, balance, isLoading, handleLogout }
}