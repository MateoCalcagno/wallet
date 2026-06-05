import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'

function Deposit() {
  const [amount, setAmount] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
      e.preventDefault()
      try {
        await api.post(`/wallet/deposit?amount=${amount}`)
        setSuccess(true)
        setTimeout(() => navigate('/dashboard'), 1500)
      } catch (err) {
        setError('Error al depositar')
      }
  }

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="bg-white p-8 rounded-xl shadow-md w-full max-w-md">
        <h1 className="text-2xl font-bold text-center text-blue-600 mb-6">Depositar</h1>

        {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
        {success && <p className="text-green-500 text-sm mb-4">Depósito exitoso, redirigiendo...</p>}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Monto</label>
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className="mt-1 w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              min="1"
            />
          </div>
          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition"
          >
            Depositar
          </button>
          <button
            type="button"
            onClick={() => navigate('/dashboard')}
            className="w-full text-sm text-gray-500 hover:text-gray-700 transition"
          >
            Volver
          </button>
        </form>
      </div>
    </div>
  )
}

export default Deposit