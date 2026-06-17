import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AuthPanel from '../components/AuthPanel'
import api from '../api/axios'

function Deposit() {
  const [amount, setAmount] = useState('')
  const [paymentMethod, setPaymentMethod] = useState('BANK_TRANSFER')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const navigate = useNavigate()

  const methods = [
    { value: 'BANK_TRANSFER', label: 'Transferencia bancaria', commission: 'Sin comisión' },
    { value: 'DEBIT_CARD', label: 'Tarjeta de débito', commission: '1% de comisión' },
    { value: 'CREDIT_CARD', label: 'Tarjeta de crédito', commission: '3% de comisión' },
  ]

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    const parsed = parseFloat(amount)
    if (isNaN(parsed) || parsed <= 0) {
      setError('Ingresá un monto válido')
      return
    }
    try {
      await api.post('/wallet/deposit', { amount: parsed, paymentMethod })
      setSuccess(true)
      setTimeout(() => navigate('/dashboard'), 1500)
    } catch (err) {
      setError(err.response?.data?.message || 'Error al depositar')
    }
  }

  return (
    <div className="min-h-screen flex">

      {/* Izquierda */}
      <AuthPanel title={"Depositá dinero,\nal instante."} subtitle="Elegí tu método de pago y agregá saldo en segundos." />

      {/* Derecha */}
      <div className="flex-1 flex flex-col justify-center px-8 md:px-14 bg-white">
        <div className="max-w-sm w-full mx-auto">

          <div className="mb-8">
            <h1 className="text-xl font-medium text-gray-900 mb-1">Depositar</h1>
            <p className="text-sm text-gray-500">Elegí el método y el monto a acreditar</p>
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

            {/* Método de pago */}
            <div>
              <label className="block text-xs font-medium text-gray-500 mb-2">Método de pago</label>
              <div className="flex flex-col gap-2">
                {methods.map((m) => (
                  <label
                    key={m.value}
                    className={`flex items-center justify-between px-4 py-3 rounded-lg border cursor-pointer transition
                      ${paymentMethod === m.value
                        ? 'border-blue-500 bg-blue-50'
                        : 'border-gray-200 bg-gray-50 hover:border-gray-300'
                      }`}
                  >
                    <div className="flex items-center gap-3">
                      <input
                        type="radio"
                        name="paymentMethod"
                        value={m.value}
                        checked={paymentMethod === m.value}
                        onChange={() => setPaymentMethod(m.value)}
                        className="accent-blue-500"
                      />
                      <span className="text-sm text-gray-800">{m.label}</span>
                    </div>
                    <span className={`text-xs font-medium ${m.value === 'BANK_TRANSFER' ? 'text-green-500' : 'text-orange-400'}`}>
                      {m.commission}
                    </span>
                  </label>
                ))}
              </div>
            </div>

            {/* Monto */}
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
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2.5 rounded-lg text-sm font-medium transition mt-2 cursor-pointer"
            >
              Depositar
            </button>

            <button
              type="button"
              onClick={() => navigate('/dashboard')}
              className="w-full text-sm text-gray-500 hover:text-gray-700 transition py-1 cursor-pointer"
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