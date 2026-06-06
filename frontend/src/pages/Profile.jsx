import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'

function Profile() {
  const [alias, setAlias] = useState('')
  const [currentAlias, setCurrentAlias] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    api.get('/users/me').then(res => {
      setCurrentAlias(res.data.alias || '')
      setAlias(res.data.alias || '')
    }).catch(() => navigate('/login'))
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess(false)

    if (!/^[a-zA-Z]+\.[a-zA-Z]+\.[a-zA-Z]+$/.test(alias)) {
      setError('El alias debe tener el formato palabra.palabra.palabra')
      return
    }

    try {
      await api.patch('/wallet/alias', { alias })
      setCurrentAlias(alias)
      setSuccess(true)
    } catch (err) {
      setError(err.response?.data?.message || 'Error al actualizar el alias')
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
            Tu alias,<br />tu identidad.
          </h2>
          <p className="text-slate-400 text-sm leading-relaxed">
            Personalizá tu alias para recibir transferencias más fácil.
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
            <h1 className="text-xl font-medium text-gray-900 mb-1">Editar alias</h1>
            <p className="text-sm text-gray-500">
              Alias actual: <span className="font-mono text-gray-700">{currentAlias || '...'}</span>
            </p>
          </div>

          {error && (
            <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-4 px-4 py-3 bg-green-50 border border-green-200 rounded-lg text-sm text-green-600">
              ¡Alias actualizado correctamente!
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1.5">Nuevo alias</label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                  </svg>
                </span>
                <input
                  type="text"
                  value={alias}
                  onChange={(e) => setAlias(e.target.value.toLowerCase())}
                  placeholder="palabra.palabra.palabra"
                  className="w-full pl-9 pr-3 py-2.5 text-sm font-mono border border-gray-200 rounded-lg bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                />
              </div>
              <p className="text-xs text-gray-400 mt-1.5">
                Solo letras separadas por puntos. Ej: <span className="font-mono">sol.luna.rio</span>
              </p>
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2.5 rounded-lg text-sm font-medium transition mt-2"
            >
              Guardar alias
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

export default Profile