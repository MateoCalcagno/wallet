import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'
import FormPage from '../components/FormPage'
import IconInput from '../components/IconInput'
import { useAmountInput } from '../hooks/useAmountInput'

function Transfer() {
  const { display, parsed: amount, handleChange } = useAmountInput()
  const [identifier, setIdentifier] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const navigate = useNavigate()

  const MAX_TRANSFER = 100000
  const esCBU = /^\d{22}$/.test(identifier.trim())
  const esAlias = /^[a-zA-Z0-9.]{6,20}$/.test(identifier.trim())

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (!identifier.trim()) {
      setError('Ingresá un CBU o alias')
      return
    }

    if (!esCBU && !esAlias) {
      setError('CBU inválido (22 dígitos) o alias inválido (6 a 20 caracteres)')
      return
    }

    if (!amount || amount <= 0) {
      setError('Ingresá un monto válido')
      return
    }
    if (amount > MAX_TRANSFER) {
      setError('El monto máximo por transferencia es $100.000')
      return
    }

    try {
      await api.post('/transactions/transfer', { identifier, amount })
      setSuccess(true)
      setTimeout(() => navigate('/dashboard'), 1500)
    } catch (err) {
      const msg = err.response?.data?.message || ''
      if (msg.toLowerCase().includes('destination wallet not found')) {
        setError('No se encontró ninguna cuenta con ese CBU o alias')
      } else if (msg.toLowerCase().includes('insufficient')) {
        setError('Saldo insuficiente para realizar la transferencia')
      } else {
        setError('Error al transferir')
      }
    }
  }

  return (
    <FormPage panelTitle={"Transferí dinero\nen segundos."} panelSubtitle="Usá el CBU o alias del destinatario.">
          <div className="mb-8">
            <h1 className="text-xl font-medium text-gray-900 mb-1">Transferir</h1>
            <p className="text-sm text-gray-500">Ingresá el CBU o alias del destinatario</p>
          </div>

          {error && (
            <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-4 px-4 py-3 bg-green-50 border border-green-200 rounded-lg text-sm text-green-600">
              ¡Transferencia exitosa! Redirigiendo...
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1.5">CBU o Alias</label>
              <IconInput
                type="text"
                value={identifier}
                onChange={e => setIdentifier(e.target.value)}
                placeholder="sol.luna.rio  ó  1234567890123456789012"
                maxLength={22}
                iconPath="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1.5">Monto</label>
              <IconInput
                type="text"
                inputMode="numeric"
                value={display}
                onChange={handleChange}
                placeholder="0,00"
                iconPath="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2.5 rounded-lg text-sm font-medium transition mt-2 cursor-pointer"
            >
              Transferir
            </button>

            <button
              type="button"
              onClick={() => navigate('/dashboard')}
              className="w-full text-sm text-gray-500 hover:text-gray-700 transition py-1 cursor-pointer"
            >
              ← Volver al inicio
            </button>
          </form>
    </FormPage>
  )
}

export default Transfer