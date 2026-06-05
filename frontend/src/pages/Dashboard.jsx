import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'

function Dashboard() {
  const [balance, setBalance] = useState(null)
  const [history, setHistory] = useState([])
  const navigate = useNavigate()

  const getUserIdFromToken = () => {
    const token = localStorage.getItem('token')
    if (!token) return null
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.sub
  }

  const fetchData = async () => {
      try {
        const [meRes, balanceRes, historyRes] = await Promise.all([
          api.get('/users/me'),
          api.get('/wallet/me'),
          api.get('/transactions/history')
        ])
        setBalance(balanceRes.data)
        setHistory(historyRes.data)
      } catch (err) {
        navigate('/login')
      }
  }

  useEffect(() => {
    fetchData()
  }, [])

  const handleLogout = () => {
    localStorage.removeItem('token')
    navigate('/login')
  }

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <div className="max-w-2xl mx-auto space-y-6">

        {/* Header */}
        <div className="flex justify-between items-center">
          <h1 className="text-2xl font-bold text-blue-600">Mi Wallet</h1>
          <button
            onClick={handleLogout}
            className="text-sm text-gray-500 hover:text-red-500 transition"
          >
            Cerrar sesión
          </button>
        </div>

        {/* Saldo */}
        <div className="bg-white rounded-xl shadow-md p-6">
          <p className="text-sm text-gray-500">Saldo disponible</p>
          <p className="text-4xl font-bold text-gray-800 mt-1">
            ${balance !== null ? balance.toFixed(2) : '...'}
          </p>
          <div className="flex gap-3 mt-4">
            <button
              onClick={() => navigate('/deposit')}
              className="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition"
            >
              Depositar
            </button>
            <button
              onClick={() => navigate('/transfer')}
              className="flex-1 bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 transition"
            >
              Transferir
            </button>
          </div>
        </div>

        {/* Historial */}
        <div className="bg-white rounded-xl shadow-md p-6">
          <h2 className="text-lg font-semibold text-gray-700 mb-4">Historial</h2>
          {history.length === 0 ? (
            <p className="text-sm text-gray-400">No hay transacciones aún.</p>
          ) : (
            <ul className="space-y-3">
              {history.map((t) => (
                <li key={t.id} className="flex justify-between items-center border-b pb-2">
                  <div>
                    <p className="text-sm font-medium text-gray-700">{t.type}</p>
                    <p className="text-xs text-gray-400">{new Date(t.createdAt).toLocaleString()}</p>
                  </div>
                  <p className={`font-semibold ${t.direction === 'SENT' ? 'text-red-500' : 'text-green-500'}`}>
                    {t.direction === 'SENT' ? '-' : '+'}${t.amount.toFixed(2)}
                  </p>
                </li>
              ))}
            </ul>
          )}
        </div>

      </div>
    </div>
  )
}

export default Dashboard