import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'

function Withdraw() {
  const [amount, setAmount] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()

    const parsed = parseFloat(amount)
    if (isNaN(parsed) || parsed <= 0) {
      setError('Ingresá un monto válido')
      return
    }

    try {
      await api.post('/wallet/withdraw', { amount: parsed })
      setSuccess(true)
      setTimeout(() => navigate('/dashboard'), 1500)
    } catch {
      setError('Error al retirar, verificá saldo')
    }
  }

  return (
    <div className="min-h-screen flex">

      {/* IZQUIERDA */}
      <div className="hidden md:flex w-5/12 bg-slate-900 flex-col justify-between p-10 relative overflow-hidden">
        <div className="absolute -top-20 -right-20 w-60 h-60 rounded-full bg-red-900 opacity-30" />
        <div className="absolute -bottom-16 -left-16 w-44 h-44 rounded-full bg-red-900 opacity-20" />

        <div className="text-white font-medium z-10">Wallet</div>

        <div className="z-10">
          <h2 className="text-white text-2xl font-medium">
            Retirá tu dinero<br />cuando quieras
          </h2>
          <p className="text-slate-400 text-sm mt-2">
            Mové fondos a tu cuenta externa fácilmente.
          </p>
        </div>

        <div className="z-10 text-xs text-slate-600">Wallet App · v1.0</div>
      </div>

      {/* DERECHA */}
      <div className="flex-1 flex flex-col justify-center px-8 md:px-14 bg-white">
        <div className="max-w-sm w-full mx-auto">

          <h1 className="text-xl font-medium mb-1">Retirar dinero</h1>
          <p className="text-sm text-gray-500 mb-8">
            Ingresá el monto a retirar
          </p>

          {error && (
            <div className="mb-3 text-sm text-red-500">{error}</div>
          )}

          {success && (
            <div className="mb-3 text-sm text-green-500">
              Retiro exitoso
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">

            <div>
              <label className="block text-xs text-gray-500 mb-1.5">
                Monto
              </label>
              <input
                type="number"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                placeholder="0.00"
                className="w-full px-3 py-2.5 border rounded-lg bg-gray-50 focus:bg-white focus:ring-2 focus:ring-red-500"
              />
            </div>

            <button className="w-full bg-red-500 hover:bg-red-600 text-white py-2.5 rounded-lg text-sm font-medium transition">
              Retirar
            </button>

            <button
              type="button"
              onClick={() => navigate('/dashboard')}
              className="w-full text-sm text-gray-500 hover:text-gray-700"
            >
              Volver
            </button>

          </form>

        </div>
      </div>
    </div>
  )
}

export default Withdraw