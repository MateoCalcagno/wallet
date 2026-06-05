import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'

function Register() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await api.post('/users', { email, password })
      navigate('/login')
    } catch {
      setError('Error al registrarse, el email puede estar en uso')
    }
  }

  return (
    <div className="min-h-screen flex">

      {/* IZQUIERDA */}
      <div className="hidden md:flex w-5/12 bg-slate-900 flex-col justify-between p-10 relative overflow-hidden">
        <div className="absolute -top-20 -right-20 w-60 h-60 rounded-full bg-blue-900 opacity-40" />
        <div className="absolute -bottom-16 -left-16 w-44 h-44 rounded-full bg-blue-900 opacity-30" />

        <div className="flex items-center gap-3 z-10">
          <div className="w-9 h-9 bg-blue-500 rounded-xl flex items-center justify-center">
            <div className="w-4 h-4 bg-white rounded-sm" />
          </div>
          <span className="text-white text-base font-medium">Wallet</span>
        </div>

        <div className="z-10">
          <h2 className="text-white text-2xl font-medium leading-snug mb-3">
            Creá tu cuenta,<br />en segundos.
          </h2>
          <p className="text-slate-400 text-sm leading-relaxed">
            Empezá a usar tu billetera digital de forma simple y segura.
          </p>
        </div>

        <div className="z-10">
          <p className="text-slate-600 text-xs">Wallet App · v1.0</p>
        </div>
      </div>

      {/* DERECHA */}
      <div className="flex-1 flex flex-col justify-center px-8 md:px-14 bg-white">
        <div className="max-w-sm w-full mx-auto">

          <div className="mb-8">
            <h1 className="text-xl font-medium text-gray-900 mb-1">
              Crear cuenta
            </h1>
            <p className="text-sm text-gray-500">
              Registrate para comenzar
            </p>
          </div>

          {error && (
            <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">

            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1.5">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="tu@email.com"
                className="w-full pl-3 pr-3 py-2.5 text-sm border border-gray-200 rounded-lg bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1.5">
                Contraseña
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full pl-3 pr-3 py-2.5 text-sm border border-gray-200 rounded-lg bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
              />
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2.5 rounded-lg text-sm font-medium transition"
            >
              Registrarse
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-6">
            ¿Ya tenés cuenta?{" "}
            <span
              onClick={() => navigate('/login')}
              className="text-blue-600 font-medium cursor-pointer hover:underline"
            >
              Iniciá sesión
            </span>
          </p>

        </div>
      </div>
    </div>
  )
}

export default Register