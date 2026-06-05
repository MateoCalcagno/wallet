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
      await api.post('/wallet/deposit', { amount: parseFloat(amount) })
      setSuccess(true)
      setTimeout(() => navigate('/dashboard'), 1500)
    } catch {
      setError('Error al depositar')
    }
  }

  return (
    <div className="min-h-screen flex">

      <div className="hidden md:flex w-5/12 bg-slate-900 flex-col justify-between p-10 relative overflow-hidden">
        <div className="absolute -top-20 -right-20 w-60 h-60 rounded-full bg-blue-900 opacity-40" />
        <div className="absolute -bottom-16 -left-16 w-44 h-44 rounded-full bg-blue-900 opacity-30" />

        <div className="text-white font-medium z-10">Wallet</div>

        <div className="z-10">
          <h2 className="text-white text-2xl">Depositá dinero</h2>
          <p className="text-slate-400 text-sm">
            Agregá saldo a tu cuenta en segundos.
          </p>
        </div>

        <div className="z-10 text-xs text-slate-600">Wallet App</div>
      </div>

      <div className="flex-1 flex flex-col justify-center px-8 md:px-14 bg-white">
        <div className="max-w-sm w-full mx-auto">

          <h1 className="text-xl font-medium mb-1">Depositar</h1>
          <p className="text-sm text-gray-500 mb-8">Ingresá el monto</p>

          {error && <p className="text-red-500 text-sm mb-3">{error}</p>}
          {success && <p className="text-green-500 text-sm mb-3">Depósito exitoso</p>}

          <form onSubmit={handleSubmit} className="space-y-4">

            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="Monto"
              className="w-full px-3 py-2.5 border rounded-lg bg-gray-50 focus:bg-white focus:ring-2 focus:ring-blue-500"
            />

            <button className="w-full bg-blue-600 text-white py-2.5 rounded-lg hover:bg-blue-700">
              Depositar
            </button>

            <button
              type="button"
              onClick={() => navigate('/dashboard')}
              className="w-full text-sm text-gray-500"
            >
              Volver
            </button>

          </form>

        </div>
      </div>
    </div>
  )
}

export default Deposit