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
    setError('')
    const parsed = parseFloat(amount)
    if (isNaN(parsed) || parsed <= 0) {
      setError('Ingresá un monto válido')
      return
    }
    try {
      await api.post('/wallet/deposit', { amount: parsed })
      setSuccess(true)
      setTimeout(() => navigate('/dashboard'), 1500)
    } catch (err) {
      setError(err.response?.data?.message || 'Error al depositar')
    }
  }

  return (
    <div className="min-h-screen flex">

      {/* Izquierda */}
      <div className="hidden md:flex w-5/12 bg-slate-900 flex-col justify-between p-10 relative overflow-hidden">
        <div className="absolute -top-20 -right-20 w-60 h-60 rounded-full bg-blue-900 opacity-40" />
        <div className="absolute -bottom-16 -left-16 w-44 h-44 rounded-full bg-blue-900 opacity-30" />

        <div className="flex items-center gap-3 z-10">
          <div className="w-9 h-9 bg-blue-500 rounded-xl flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
            </svg>
          </div>
          <span className="text-white text-base font-medium">Wallet</span>
        </div>

        <div className="z-10">
          <h2 className="text-white text-2xl font-medium leading-snug mb-3">
            Depositá dinero,<br />al instante.
          </h2>
          <p className="text-slate-400 text-sm leading-relaxed">
            Agregá saldo a tu cuenta en segundos.
          </p>
        </div>

        <div className="z-10">
          <p className="text-slate-600 text-xs">Wallet App · v1.0</p>
        </div>
      </div>

      {/* Derecha */}
      <div className="flex-1 flex flex-col justify-center px-8 md:px-14 bg-white">
        <div className="max-w-sm w-full mx-auto">

          <div className="mb-8">
            <h1 className="text-xl font-medium text-gray-900 mb-1">Depositar</h1>
            <p className="text-sm text-gray-500">Ingresá el monto a acreditar</p>
          </div>

          {error && (
            <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-4 px-4 py-3 bg-green-50 border border-green-200 rounded-lg text-sm text-green-600">
              ¡Depósito exitoso! Redirigiendo...
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1.5">Monto</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </span>
                <input
                  type="number"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  placeholder="0.00"
                  min="0.01"
                  step="0.01"
                  className="w-full pl-9 pr-3 py-2.5 text-sm border border-gray-200 rounded-lg bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2.5 rounded-lg text-sm font-medium transition mt-2"
            >
              Depositar
            </button>

            <button
              type="button"
              onClick={() => navigate('/dashboard')}
              className="w-full text-sm text-gray-500 hover:text-gray-700 transition py-1"
            >
              ← Volver al inicio
            </button>
          </form>

        </div>
      </div>
    </div>
  )
}

export default Deposit